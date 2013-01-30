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
              (content-type "text/csv;charset=utf-8"
                            (data/create-csv
                             (str/replace file #".csv" ""))))
            
            (re-find #".spm" file)
            (do
              (log/info user "downloading spm file:" file)
              (content-type "text/plain;charset=utf-8"
                            (data/markdown
                             (str/replace file #".spm" ""))))
            
            :else
            (throw (FileNotFoundException.)))
      (catch FileNotFoundException _
        (log/error user "requested file:" file ".File not found.")
        (redirect "/data")))))
