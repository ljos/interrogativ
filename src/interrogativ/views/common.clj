(ns interrogativ.views.common
  (:use [hiccup.page :only [include-js include-css]]
        [noir.core :only [defpartial]]))

(def jquery "http://code.jquery.com/")

(defpartial body [& content]
  [:body content])

(defpartial layout [{:keys [title body]}]
  [:head
   [:title title]
   [:meta
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-css "/css/common.css")
    (include-js (str jquery "jquery-1.8.2.js"))
    (include-js "/bootstrap/js/bootstrap.min.js")]]
  body)
