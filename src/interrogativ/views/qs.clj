(ns interrogativ.views.qs
  (:require [clojure.tools.logging :as log]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))


(defpage "/qs/:page" [{:keys [page]}]
  (get @data/pages (keyword page)))


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
