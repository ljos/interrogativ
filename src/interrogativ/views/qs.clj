(ns interrogativ.views.qs
  (:require [clojure.tools.logging :as log]
            [noir.cookies :as cookies]
            [interrogativ.models.data :as data])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))

(defpage "/qs/:page" {:keys [page]}
  (log/info "Serving page" page)
  (log/info "getting cookie: " (cookies/get :tracker))
  ;; (if (cookies/get :tracker)
  ;;   (redirect (str page "/takk"))
  (data/survey-for-name page)
  ;; )
  )

(defpage [:post "/qs/:page"] data
  (let [page (:page data)
        submitter-id (data/new-submitter-id!)]
    (cookies/put! :tracker
      {:value submitter-id
       :path page
       :max-age 86400})
    (data/store-answer
     page
     (-> data
         (dissoc :page)
         (dissoc :submitter)
         (assoc :informant submitter-id)))
    (redirect (str page "/takk"))))

(defpage "/qs/:page/takk" {:keys [page]}
  (data/thankyou-for-name page))
