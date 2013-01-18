(ns interrogativ.views.edit
  (:require [interrogativ.views.common :as common]
            [noir.session :as session]
            [clojure.tools.logging :as log])
  (:import [java.io FileNotFoundException])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect]]
        [hiccup.page :only [include-js include-css]]))

(pre-route "/edit*" {}
  (if-not (session/get :admin)
    (redirect "/login")))

(defpage "/edit/:page" {:keys [page]}
  (log/info "Editing page:" page)
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
              [:a {:href (str "/data/" page)
                   :class "btn btn-danger"
                   :type "button"}
               [:i {:class "icon-remove-circle icon-white"}]
               " Cancel"]]]
            [:input {:type "hidden"
                     :id "text"
                     :name "text"}]
            [:input {:type "hidden"
                     :name "page"
                     :value page}]
            [:div {:id "editor"}
             (try
               (slurp (str "qs/" page ".spm"))
               (catch FileNotFoundException _
                 (log/info "File not found: "
                           (str "qs/" page ".spm"))))]]
           (include-js (str "http://d1n0x3qji82z53.cloudfront.net/"
                            "src-min-noconflict/ace.js"))
           (include-js "/cljs/editor.js"))}))

