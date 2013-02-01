(ns interrogativ.views.upload
  (:require [clojure.string :as str]
            [taoensso.timbre :as log]
            [interrogativ.models.data :as data]
            [interrogativ.models.spm :as spm]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [defroutes POST]]
            [noir.session :as session]
            [interrogativ.util :as util])
  (:use [noir.response :only [redirect]]))

(defn upload [data]
  (log/info "Uploading data" (or (:file data) (:text data)))
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

(defroutes upload-routes
  (wrap-multipart-params
   (POST "/upload" [page file text] (upload {:page page :file file :text text}))))
