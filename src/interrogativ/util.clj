(ns interrogativ.util
  (:require [noir.session :as session]
            [noir.response :refer [redirect]]))

(defmacro private [& body]
  `(if (session/get :user)
     (do ~@body)
     (redirect "/login")))
