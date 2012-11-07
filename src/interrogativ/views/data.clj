(ns interrogativ.views.data
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [interrogativ.views.spm :as spm]
            [noir.session :as session]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import [java.io FileNotFoundException])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect content-type]]
        [hiccup.core :only [html]]
        [hiccup.page :only [include-js include-css]]))


(pre-route "/data" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(pre-route "/data/*" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(defpage "/data/" {}
  (redirect "/data"))

(defn directory-to-links [dir]
  (for [f (.listFiles (io/file (str "db/" dir)))
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
                      :id "file-chooser"
                      :name "file"}]
             [:div {:class "input-prepend"
                    :id "cf-btn"}
              [:button {:class "btn btn-primary"
                        :id "choose-btn"
                        :type "button"}
               "Choose file"]
              [:span {:class "input-xlarge uneditable-input"
                      :type "text"}]]
             [:div {:class "input-prepend"
                    :id "uc-btn"}
              [:button {:class "btn btn-inverse"
                        :id "cancel-btn"
                        :type "button"}
               "&times;"]
              [:button {:class "btn btn-primary"
                        :id "upload-btn"
                        :type "submit"}
               "Add"]
              [:span {:class "input-xlarge uneditable-input"
                      :type "text"
                      :id "text"}]]]]
           (include-js "/js/upload-file.js"))}))
