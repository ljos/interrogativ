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
  (data/survey page)
  ;; )
  )

(defpage [:post "/qs/:page"] data
  (let [page (:page data)]
    (cookies/put! :tracker
      {:value (data/store-answer
               page
               (-> data
                   (dissoc :page)
                   (dissoc :submitter)))
       :path page
       :max-age 86400})
    (redirect (str page "/takk"))))

(defpage "/qs/:page/takk" {:keys [page]}
  (data/thankyou page))
