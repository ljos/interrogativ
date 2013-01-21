(ns interrogativ.views.download
  (:require [interrogativ.models.data :as data]
            [noir.session :as session]
            [clojure.string :as str]
            [clojure.tools.logging :as log])
  (:import [java.io FileNotFoundException])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect content-type]]))

(pre-route "/download/*" {}
  (if-not (session/get :user)
    (redirect "login")))

(defpage "/download/:file" {:keys [file]}
  (let [user (session/get :user)]
    (try
      (cond (re-find #".csv" file)
            (do
              (log/info  user "downloading CSV file:" file)
              (->> (-> file
                       (str/replace #"\.csv" ".dat")
                       (str/replace "_" "/"))
                   (str "db/")
                   data/create-csv-from-file
                   (content-type "text/csv")))
            
            (re-find #".spm" file)
            (do
              (log/info user "downloading spm file:" file)
              (->> file
                   (str "qs/")
                   slurp
                   (content-type "text/plain")))
            
            :else
            (throw (FileNotFoundException.)))
      (catch FileNotFoundException _
        (log/error user "requested file:" file ".File not found.")
        (redirect "/data")))))

