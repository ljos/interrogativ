(ns interrogativ.views.qs
  (:require [clojure.tools.logging :as log]
            [noir.cookies :as cookies]
            [interrogativ.models.data :as data])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))

(defpage "/qs/:page" {:keys [page]}
  (let [page (str "/qs/" page)]
    (log/info "getting cookie: " (cookies/get :tracker))
    (if (cookies/get :tracker)
      (redirect (str page "/takk"))
      (:survey (data/survey-for-name page)))))

(defpage [:post "/qs/:page"] data
  (let [page (str "/qs/" (:page data))
        submitter-id (data/generate-submitter-id page)]
    (cookies/put! :tracker
      {:value submitter-id
       :path page
       :max-age 86400})
    (data/store-answer
     (-> data
         (dissoc :page)
         (dissoc :submitter)
         (#(reduce (fn [dat k]
                     (assoc dat
                       (if (string? k) k (name k))
                       (get % k)))
                   {}
                   (keys %)))
         (assoc :informant submitter-id))
     page)
    (redirect (str page "/takk"))))

(defpage "/qs/:page/takk" {:keys [page]}
  (:post (data/survey-for-name (str "/qs/" page))))
