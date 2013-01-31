(ns interrogativ.views.edit
  (:require [clojure.tools.logging :as log]
            [interrogativ.views.common :as common]
            [noir.session :as session]
            [interrogativ.models.data :as data]
            [compojure.core :refer [defroutes GET]]
            [interrogativ.util :as util])
  (:use [hiccup.page :only [include-js include-css]]
        [noir.response :only [redirect]]))

(defn edit [page]
  (log/info (session/get :user) "editing page:" page)
  (if (data/owner? page)
    (common/layout
     {:title (str "Edit " page)
      :body (common/body
             (include-css "/css/editor.css")
             [:form {:name "editor"
                     :action "/upload"
                     :method "POST"}
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
               (data/markdown page)]]
             (include-js "/ace/ace.js")
             (include-js "/cljs/editor.js"))})
    (redirect "/data")))

(defroutes edit-routes
  (GET "/edit/:page" [page] (util/private (edit page))))
