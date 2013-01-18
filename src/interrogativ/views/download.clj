(ns interrogativ.views.download
  (:require [interrogativ.models.data :as data]
            [noir.session :as session]
            [clojure.string :as str]
            [clojure.tools.logging :as log])
  (:import [java.io FileNotFoundException])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect content-type]]))

(pre-route "/download/*" {}
  (if-not (session/get :admin)
    (redirect "login")))

(defpage "/download/:file" {:keys [file]}
  (try
    (cond (re-find #".csv" file)
          (do
            (log/info "Serving CSV file:" file)
            (->> (-> file
                     (str/replace #"\.csv" ".dat")
                     (str/replace "_" "/"))
                 (str "db/")
                 data/create-csv-from-file
                 (content-type "text/csv")))

          (re-find #".spm" file)
          (do
            (log/info "Serving spm file:" file)
            (->> file
                 (str "qs/")
                 slurp
                 (content-type "text/plain")))

          :else
          (throw (FileNotFoundException.)))
    (catch FileNotFoundException _
      (log/error "File not found:" file)
      (redirect "/data"))))

