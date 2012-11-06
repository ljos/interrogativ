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
        [hiccup.core :only [html]]
        [hiccup.page :only [include-js include-css]]))

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

(defpage "/edit/:page" {:keys [page]}
  (common/layout
   {:title (str "Edit " page)
    :body (common/body
           (include-css "/css/editor.css")
           [:form {:name "editor"
                   :action "/upload"
                   :method "post"}
            [:ul {:class "breadcrumb"}
             [:li
              [:a {:href "/data"} "Pages"]
              [:span {:class "divider"} "/"]]
             [:li
              [:a {:href (str "/data/" page)}
               page]
              [:span {:class "divider"} "/"]]
             [:li {:class "active"}
              "edit"]
             [:br]
             [:div {:class "btn-toolbar"
                    :align "right"}
              [:button {:class "btn btn-primary"
                        :id "save"
                        :type "submit"}
               [:i {:class "icon-upload icon-white"}]
               " Save"]
              [:button {:href (str "/data/" page)
                        :class "btn btn-danger"}
               [:i {:class "icon-remove-circle icon-white"}]
               " Cancel"]]]
            [:input {:type "hidden"
                     :id "text"
                     :name "text"}]
            [:input {:type "hidden"
                     :name "page"
                     :value page}]
            [:div {:id "editor"}
             (slurp (str "qs/" page ".spm"))]]
           (include-js (str "http://d1n0x3qji82z53.cloudfront.net/"
                            "src-min-noconflict/ace.js"))
           (include-js "/js/editor-save.js"))}))

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
        :let [name (-> (.getName f)
                       (str/replace #"\.dat$" ".csv"))]]
    (html
     [:a {:href (str "/download/"
                     (-> (str dir "/" name)
                         (str/replace "/" "_")))}
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
             [:fieldset 
              [:legend "Data"
               [:span {:class "divider"} " : "]
             [:ul {:class "nav nav-pills"}
              [:li {:class "disabled"}
               [:a "Data"
                [:span {:class "divider"} " : "]]]
              [:li
               [:a {:href (if frontpage
                            "/" (str "/qs/" page))}
                "page"]]
              (when-not frontpage
                (list [:li
                       [:a {:href (str "/edit/" page)}
                        "edit"]]
                      [:li
                       [:a {:href (str "/download/" page ".spm" )}
                        "download"]]))]
             [:div {:class "links"}
              [:hr {:style "margin-top:-1em;"}]
              (directory-to-links
               (if frontpage
                 "" (str "qs/" page)))])})))

(defpage "/data" {}
  (common/layout
   {:title "Data"
    :body (common/body
           [:ul {:class "breadcrumb"}
            [:li {:class "active"} "Pages"]]
           [:fieldset
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
            (->> file
                 (str "qs/")
                 slurp
                 (content-type "text/plain")))

          :else
          (throw (FileNotFoundException.)))
    (catch FileNotFoundException _
      (log/error "File not found:" file)
      (redirect "/data"))))

(pre-route "/upload" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(defpage [:post "/upload"] data
  (cond (:file data)
        (let [file (:file data)]
          (when-let [filename (not-empty (:filename file))]
            (data/upload-file file)
            (spm/create-page-from (str "qs/" filename)))
          (redirect "/data"))

        (:text data)
        (let [page (:page data)
              text (str/replace (:text data) "\r" "")
              file (str "qs/" page ".spm")]
          (log/info "Uploading revision to " file)
          (spit file text)
          (spm/create-page-from file)
          (redirect (str "/data/" page)))

        :else
        (redirect "/data")))
