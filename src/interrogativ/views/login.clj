(ns interrogativ.views.login
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [interrogativ.models.data :as data]
            [interrogativ.views.common :as common]
            [noir.session :as session]
            [compojure.core :refer [defroutes GET POST]])
  (:use [noir.response :only [redirect]]))

(defn login
  ([]
     (common/layout
      {:title "Login"
       :body (common/body           
              [:fieldset 
               [:legend "Login"]
               [:form {:action "/login"
                       :method "POST"}
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
  ([uname pword]
     (if (data/valid-user? uname pword)
       (do
         (session/put! :user uname)
         (log/info uname "logged in.")
         (redirect "/data"))
       (do
         (log/info "invalid user login: " uname pword)
         (redirect "/login")))))



(defroutes login-routes
  (POST "/login" [uname pword] (login uname pword))
  (GET "/login" [] (login)))
