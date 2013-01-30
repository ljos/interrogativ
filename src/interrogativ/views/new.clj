(ns interrogativ.views.new
  (:require [clojure.string :as str]
            [noir.session :as session]
            [clojure.tools.logging :as log]
            [interrogativ.models.spm :as spm]
            [interrogativ.models.data :as data]
            [interrogativ.views.common :as common])
  (:use [noir.response :only [redirect]]
        [noir.core :only [defpage pre-route]]))

(pre-route "/new" {}
  (if-not (session/get :user)
    (redirect "/login")))

(defpage [:post "/new"] {:keys [new]}
  (if (str/blank? new)
    (redirect "/data")
    (let []
      (log/info (session/get :user)
                "created new survey:"
                new)
      (data/insert-survey new "")
      (redirect (str "/data/" new)))))
