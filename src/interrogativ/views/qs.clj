(ns interrogativ.views.qs
  (:require [clojure.tools.logging :as log]
            [noir.cookies :as cookies]
            [interrogativ.models.data :as data]
            [compojure.core :refer [defroutes GET POST]])
  (:use [noir.response :only [redirect]]))

(defn view-qs [page]
  (log/info "Serving page" page)
  (log/info "getting cookie: " (cookies/get :tracker))
  ;; (if (cookies/get :tracker)
  ;;   (redirect (str page "/takk"))
  (data/survey page)
  ;; )
  )

(defn post-data [page data] 
  (cookies/put! :tracker
    {:value (data/store-answer page (dissoc data "submitter"))
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
