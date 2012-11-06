(ns interrogativ.views.data
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
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
  (common/layout
   {:title "Login"
    :body (common/body           
           [:fieldset 
            [:legend "Login"]
            [:form {:action "/login"
                    :method "post"}
             [:div
              [:input {:type "text"
                       :name "uname"
                       :id "uname"
                       :placeholder "Username"}]]
             [:div
              [:input {:type "password"
                       :name "pword"
                       :id "pword"
                       :placeholder "Password"}]]
             [:div
              [:input {:class "btn btn-primary"
                       :type "submit"
                       :name "submit"
                       :id "submit"
                       :value "login"
                       :data-role "none"}]]]])}))

(defn passwd-for [user]
  (with-open [rdr (io/reader "passwd")]
    (loop [lines (line-seq rdr)]
      (cond (empty? lines)
            ""

            (re-matches (re-pattern (format "^%s:.*" user))
                        (first lines))
            (-> (first lines)
                (str/replace #"^.*?:" ""))

            :else
            (recur (rest lines))))))

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

(defn directory-to-links-ui [dir]
  (for [f (.listFiles (java.io.File. (str "db/" dir)))
        :when (not (.isDirectory f))
        :let [name (str/replace (.getName f) #"\.dat$" ".csv")]]
    (html
     [:a {:href (str "/download/"
                     (-> (str dir "/" name)
                         (str/replace "/" "_")))
          :class "csv"}
      name]
     [:br])))

(defpage "/data/:page" {:keys [page]}
  (let [frontpage (= page "frontpage")]
    (common/layout
     {:title page
      :body (common/body
             [:ul {:class "breadcrumb"}
              [:li
               [:a {:href "/data"} "Pages"]
               [:span {:class "divider"} "/"]]
              [:li {:class "active"}
               page]]
             [:fieldset {:style "margin-left:1em;margin-right:1em"}
              [:legend "Data"
               [:span {:class "divider"} " : "]
               [:a {:href (if frontpage
                            "/" (str "/qs/" page))}
                "page"]
               (when-not frontpage
                 (list [:span {:class "divider"} " / "]
                       [:a {:href (str "/download/" page ".spm" )}
                        "script"]))]
              (directory-to-links-ui
               (if frontpage
                 "" (str "qs/" page)))])})))

(defpage "/data" {}
  (common/layout
   {:title "Data"
    :body (common/body
           [:ul {:class "breadcrumb"}
            [:li {:class "active"} "Pages"]]
           [:fieldset {:style "margin-left:1em;margin-right:1em"}
            [:legend "Pages"]
            [:a {:href "/data/frontpage"}
             "frontpage"]
            [:br]
            (for [page (map #(-> % str (str/replace-first ":/qs/" ""))
                            (keys @data/domains))]
              (list [:a {:href (str"/data/" page)}
                     page]
                    [:br]))
            [:hr]
            [:form {:method "post"
                    :enctype "multipart/form-data"
                    :action "/upload"}
             [:input {:type "file"
                      :value "Choose file"}]
             [:br]
             [:input {:class "btn btn-mini"
                      :type "submit"
                      :value "Upload"}]]])}))

(pre-route "/download/*" {}
  (if-not (session/get :admin)
    (redirect "login")))

(defpage "/download/:file" {:keys [file]}
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

(defpage [:post "/upload"] {:keys [file]}
  (when-let [filename (not-empty (:filename file))]
    (data/upload-file file)
    (spm/create-page-from (str "qs/" filename)))
  (redirect "/data"))
  (if-not (session/get :admin)
    (redirect "/login")))
