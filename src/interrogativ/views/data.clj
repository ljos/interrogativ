(ns interrogativ.views.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [compojure.core :refer [defroutes GET POST]]
            [interrogativ.models.data :as data]
            [interrogativ.views.common :as common]
            [noir.session :as session]
            [interrogativ.util :as util])
  (:use [hiccup.page :only [include-js]]
        [noir.response :only [redirect]]))

(defn data-page
  ([]
     (common/layout
      {:title "Data"
       :body (common/body
              [:ul {:class "breadcrumb"}
               [:li {:class "active"} "Pages"]]
              [:fieldset
               [:legend "Pages"]
               (for [page (data/pages)]
                 (list [:a {:href (str"/data/" page)}
                        page]
                       [:br]))
               [:hr]
               [:div {:class "btn-toolbar"}
                [:form  {:method "post"
                         :action "/new"}
                 [:div {:class "input-prepend"}
                  [:button {:class "btn btn-primary"
                            :type "submit"}
                   [:i {:class "icon-plus icon-white"}]
                   " New"]
                  [:input {:id "new"
                           :name "new"
                           :class "input-xlarge"
                           :type "text"}]]]]
               [:p " or "]
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
                  "Upload "]
                 [:span {:class "input-xlarge uneditable-input"
                         :id "upload-span"
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
              (include-js "/cljs/upload.js"))}))
  ([page]
     (if (data/owner? page)
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
                  [:a {:href (str "/qs/" page)}
                   "page"]]           
                 [:li
                  [:a {:href (str "/edit/" page)}
                   "edit"]]
                 [:li
                  [:a {:href (str "/download/overview_"
                                  (str/replace page #"_" "-")
                                  ".txt")}
                   "overview"]]
                 [:li
                  [:a {:href (str "/download/" page ".spm" )}
                   "download"]]]
                [:div {:class "links"}
                 [:hr {:style "margin-top:-1em;"}]
                 (for [date (data/dates page)
                       :let [file (-> page
                                      (str/replace #"_" "-")
                                      (str "_" date ".csv"))]]
                   (list [:a {:href (str "/download/" file)}
                          file]
                         [:br]))
                 [:br]])})
       (redirect "/data"))))

(defroutes data-routes
  (GET "/data/:page" [page] (util/private (data-page page)))
  (GET "/data" [] (util/private (data-page))))
