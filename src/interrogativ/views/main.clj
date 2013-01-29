(ns interrogativ.views.main
  (:require [interrogativ.views.common :as common])
  (:use [hiccup.core :only [html]]
        [noir.core :only [defpage]]))

(defpage "/" []
  (common/layout
   {:title "Interrogativ&#8253;"
    :body (common/body
           [:h1 "Interrogativ&#8253;"])}))
