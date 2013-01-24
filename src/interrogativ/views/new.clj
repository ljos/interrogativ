(ns interrogativ.views.new
  (:require [clojure.string :as str]
            [noir.session :as session]
            [clojure.tools.logging :as log]
            [interrogativ.models.spm :as spm]
            [interrogativ.views.common :as common])
  (:use [noir.response :only [redirect]]
        [noir.core :only [defpage pre-route]]))

(pre-route "/new" {}
  (if-not (session/get :user)
    (redirect "/login")))

(defpage [:post "/new"] {:keys [new]}
  (if (str/blank? new)
    (redirect "/data")
    (let [file (str "qs/" (str/trim new)  ".spm")
          user (session/get :user)]
      (log/info user "created new survey:" file)
      (spit file "")
      (spm/create-survey-from file)
      (redirect (str "/data/" new)))))
