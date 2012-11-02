(ns interrogativ.views.data
  (:require [interrogativ.models.data :as data]
            [interrogativ.views.spm :as spm]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import [java.io FileNotFoundException])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect content-type]]
        [hiccup.core :only [html]]))

(defpage "/login" {}
  (html
   [:head
    [:title "Login"]]
   [:body
    [:form {:action "/login" :method "post"}
     [:div
      [:label {:for "uname"} "Username:"]
      [:input {:type "text"
               :name "uname"
               :id "uname"
               :value ""
               :data-role "none"}]]
     [:div
      [:label {:for "pword"} "Password:"]
      [:input {:type "password"
               :name "pword"
               :id "pword"
               :value ""
               :data-role "none"}]]
     [:div
      [:input {:type "submit"
               :name "submit"
               :id "submit"
               :value "login"
               :data-role "none"}]]]]))

(defn passwd-for [user]
  (with-open [rdr (io/reader "passwd")]
    (loop [lines (line-seq rdr)]
      (cond (empty? lines)
            ""
            (re-matches (re-pattern (format "^%s:.*" user))
                        (first lines))
            ,(str/replace (first lines) #"^.*?:" "")
            :else
            ,(recur (rest lines))))))

(defpage [:post "/login"] {:keys [uname pword]}
  (let [encrypted (passwd-for uname)]
    (if-not (or (not (empty? encrypted))
                (crypt/compare pword encrypted))
      (redirect "/login")
      (do (session/put! :admin true)
          (redirect "/data")))))

(pre-route "/data" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(pre-route "/data/*" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(defpage "/data/" {}
  (redirect "/data"))

(defn directory-to-links [dir]
  (for [f (.listFiles (java.io.File. (str "db/" dir)))
        :when (not (.isDirectory f))
        :let [name (str/replace (.getName f) #"\.dat$" ".csv")]]
    [:div {:style "text-indent:1em"}
     [:a {:href  (str "data/"
                      (-> (str dir "/" name)
                          (str/replace "/" "_")))}
      name]
     [:br]]))

(defpage "/data" {}
  (html [:p
         [:h4 [:a {:href "/"} "/"]]
         (directory-to-links "")]
        (for [page (map #(-> % str (str/replace-first ":/" ""))
                        (keys @data/domains))]
          [:p
           [:h4  [:a {:href page} page]]
           [:a {:href (str "/data/"
                           (str/replace-first page "qs/" "")
                           ".spm")}
            "Spm-file"]
           (directory-to-links page)])
        [:hr]
        [:p
         [:form {:method "post"
                 :enctype "multipart/form-data"
                 :action "/upload"}
          [:input {:type "file"
                   :name "file"}]
          [:br]
          [:input {:type "submit"
                   :value "Upload"}]]]))

(defpage "/data/:file" {:keys [file]}
  (try
    (cond (re-find #".csv" file)
          (do
            (log/info "Serving CSV file:" file)
            (->> (-> file
                     (str/replace #"\.csv" ".dat")
                     (str/replace "_" "/"))
                 (str "db/")
                 data/create-csv-from-file
                 (content-type "text/csv")))

          (re-find #".spm" file)
          (do
            (log/info "Serving spm file:" file)
            (content-type "text/plain"
                          (slurp (str "qs/" file))))
          
          :else
          (throw (FileNotFoundException.)))
    (catch FileNotFoundException _
      (log/error "File not found:" file)
      (redirect "/data"))))

(pre-route "/upload" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(defpage [:post "/upload"] {:keys [file]}
  (when-let [filename (not-empty (:filename file))]
    (data/upload-file file)
    (spm/create-page-from (str "qs/" filename)))
  (redirect "/data"))
