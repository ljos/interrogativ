(ns interrogativ.views.new
  (:require [clojure.string :as str]
            [noir.session :as session]
            [clojure.tools.logging :as log]
            [interrogativ.models.spm :as spm]
            [interrogativ.models.data :as data]
            [interrogativ.views.common :as common]
            [interrogativ.util :as util]
            [compojure.core :refer [defroutes POST]])
  (:use [noir.response :only [redirect]]))

(defn create-new [page]
  (if (str/blank? page)
    (redirect "/data")
    (let []
      (log/info (session/get :user)
                "created new survey:"
                page)
      (data/insert-survey page "")
      (redirect (str "/data/" page)))))

(defroutes new-routes
  (POST "/new" [new] (util/private (create-new new))))
