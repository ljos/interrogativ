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
            (log/info (session/get :user) "uploading file " file))
          (redirect "/data"))

        (:text data)
        (let [page (:page data)
              text (str/replace (:text data) "\r" "")]
          (log/info (session/get :user) "uploading revision to " page)
          (data/update-survey page text)
          (redirect (str "/data/" page)))

        :else
        (redirect "/data")))
