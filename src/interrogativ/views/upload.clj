(ns interrogativ.views.upload
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [interrogativ.models.data :as data]
            [interrogativ.models.spm :as spm]
            [noir.session :as session])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect]]))

(pre-route "/upload" {}
  (if-not (session/get :user)
    (redirect "/login")))

(defpage [:post "/upload"] data
  (cond (:file data)
        (let [file (:file data)]
          (when-let [filename (not-empty (:filename file))]
            (data/upload-file file)
            (log/info (session/get :user) "uploading file " file)
            (spm/create-survey-from (str "qs/" filename)))
          (redirect "/data"))

        (:text data)
        (let [page (:page data)
              text (str/replace (:text data) "\r" "")
              file (str "qs/" page ".spm")]
          (log/info (session/get :user) "uploading revision to " file)
          (spit file text)
          (spm/create-survey-from file)
          (redirect (str "/data/" page)))

        :else
        (redirect "/data")))
