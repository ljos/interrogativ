(ns interrogativ.views.common
  (:require [clojure.string :only [replace lower-case] :as string])
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-js include-css]]
        [hiccup.core :only [html]])
  (:refer-clojure :exclude [name id]))

(defpartial body [& content]
  [:body content])

(def jquery "http://code.jquery.com/")

(defpartial layout [{:keys [title body]}]
  [:head
   [:title title]
   [:meta
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-js "/bootstrap/js/bootstrap.min.js")
    (include-js (str jquery "jquery-1.8.2.js"))]]
  body)
