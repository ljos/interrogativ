(ns interrogativ.views.upload
  (:require [interrogativ.models.data :as data]
            [interrogativ.views.spm :as spm]
            [noir.session :as session]
            [clojure.string :as str]
            [clojure.tools.logging :as log])
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
            (spm/create-page-from (str "qs/" filename)))
          (redirect "/data"))

        (:text data)
        (let [page (:page data)
              text (str/replace (:text data) "\r" "")
              file (str "qs/" page ".spm")]
          (log/info "Uploading revision to " file)
          (spit file text)
          (spm/create-page-from file)
          (redirect (str "/data/" page)))

        :else
        (redirect "/data")))