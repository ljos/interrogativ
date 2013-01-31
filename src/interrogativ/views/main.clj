(ns interrogativ.views.main
  (:require [interrogativ.views.common :as common]
            [compojure.core :refer [defroutes GET]])
  (:use [hiccup.core :only [html]]))

(defroutes main-routes
  (GET "/" [] (common/layout
               {:title "Interrogativ&#8253;"
                :body (common/body
                       [:h1 "Interrogativ&#8253;"])})))
