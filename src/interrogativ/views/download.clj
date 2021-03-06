(ns interrogativ.views.download
  (:require [interrogativ.models.data :as data]
            [noir.session :as session]
            [clojure.string :as str]
            [compojure.core :refer [defroutes GET]]
            [taoensso.timbre :as log]
            [interrogativ.util :as util]
            [noir.response :refer [redirect content-type]])
  (:import [java.io FileNotFoundException]))

(defn download [file]
  (let [user (session/get :user)]
    (cond (re-find #".csv$" file)
          (let [[page date] (-> file
                                (str/replace #".csv" "")
                                (str/split #"_"))]
            (log/info  user "downloading CSV file:" file)
            (content-type "text/csv;charset=utf-8"
                          (data/csv page date)))
          
          (re-find #".spm$" file)
          (do
            (log/info user "downloading spm file:" file)
            (content-type "text/plain;charset=utf-8"
                          (data/markdown
                           (str/replace file #".spm" ""))))

          (re-find #".txt$" file)
          (let [page (-> file
                         (str/replace #".txt" "")
                         (str/replace #"overview_" ""))]
            (log/info user "downloading overview: " page)
            (content-type "text/plain;charset=utf-8"
                          (data/overview page)))
          :else
          (redirect "/data"))))

(defroutes download-routes
  (GET "/download/:file" [file] (util/private (download file))))
