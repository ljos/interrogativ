(ns interrogativ.views.common
  (:require [hiccup.page :refer [include-js include-css html5]]))

(def jquery "http://code.jquery.com/")

(defn body [& content]
  [:body content])

(defn layout [{:keys [title body]}]
  (html5
   [:head
    [:title title]
    [:meta
     (include-css "/bootstrap/css/bootstrap.min.css")
     (include-css "/css/common.css")
     (include-js (str jquery "jquery-1.8.2.js"))
     (include-js "/bootstrap/js/bootstrap.min.js")]]
   body))
