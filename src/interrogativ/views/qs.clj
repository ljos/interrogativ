(ns interrogativ.views.qs
  (:require [taoensso.timbre :as log]
            [noir.cookies :as cookies]
            [interrogativ.models.data :as data]
            [compojure.core :refer [defroutes GET POST]])
  (:use [noir.response :only [redirect]]))

(defn view-qs [page]
  (log/info "Serving page" page)
  (log/info "getting cookie: " (cookies/get :tracker))
  ;; This is ugly, we need to do this automatically later
  (if (and (= (clojure.string/lower-case page) "spent")
           (cookies/get :tracker))
    (redirect (str page "/takk"))
  (data/survey page)))

(defn post-data [page data]
  (data/store-answer page (dissoc data "submitter"))
  (cookies/put! :tracker
    {:value "blocked"
     :path page
     :max-age 86400})
  (redirect (str page "/takk")))

(defn thanks [page]
  (data/thankyou page))


(defroutes qs-routes
  (GET "/qs/:page" [page] (view-qs page))
  (GET "/qs/:page/takk" [page] (thanks page))
  (POST "/qs/:page" params (post-data (get-in params [:params :page])
                                      (:form-params params))))
