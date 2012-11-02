(ns interrogativ.views.data
  (:require [interrogativ.models.data :as data]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [clojure.string :as str]
            [clojure.java.io :as io])
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
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
                      (str/replace (str dir "/" name)
                                   "/" "_"))}
      name]
     [:br]]))

(defpage "/data" {}
  (html [:p
         [:h4 [:a {:href "/"} "/"]]
         (directory-to-links "")]
        (for [page (map #(-> % str (str/replace-first ":" ""))
                        (keys @data/domains))]
          [:p
           [:h4  [:a {:href page} page]]
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
  (log/info (str "Serving CSV file: " file))
  (content-type "text/csv"
                (data/create-csv-from-file
                 (format "db/%s"
                         (str/replace (str/replace
                                       file #"\.csv$" ".dat")
                                      "_" "/")))))

(pre-route "/upload" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(defpage [:post "/upload"] {:keys [file]}
  (data/upload-file file)
  (redirect "/data"))
