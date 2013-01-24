(ns interrogativ.views.qs
  (:require [clojure.tools.logging :as log]
            [noir.cookies :as cookies]
            [interrogativ.models.data :as data])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))


(defpage "/qs/:page" [{:keys [page]}]
  (get @data/pages (keyword page)))

(defpage "/qs/:page/takk" {:keys [page]}
  (:post (data/survey-for-name (str "/qs/" page))))

(defpage [:post "/qs/:page/takk"] data
  (let [submitter-id (data/generate-submitter-id)]
    (cookies/put! :tracker {:value submitter-id
                            :path (:page data)
                            :expires 1
                            :max-age 86400})
    (data/store-answer
     (-> data
         (dissoc :page)
         (dissoc :submitter)
         (assoc :informant submitter-id))
     (:page data))
    (redirect (:page data))))
