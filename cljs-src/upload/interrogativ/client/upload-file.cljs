(ns interrogativ.client.upload-file
  (:require [clojure.string :as str]
            [jayq.core :as jq])
  (:use [jayq.core :only [$]]))

(jq/hide ($ :#uc-btn))

(jq/bind ($ :#choose-btn) :click
      (fn []
        (.click ($ :#file-chooser))))

(jq/bind ($ :#file-chooser) :change
         (fn []
           (let [path (-> (js* "this.value")
                          (str/replace "C:\\fakepath\\" ""))]
             (jq/inner ($ :#text) path))
           (jq/show ($ :#uc-btn) "slow")
           (jq/hide ($ :#cf-btn) "slow")))

(jq/bind ($ :#cancel-btn) :click
         (fn []
           (jq/hide ($ :#uc-btn) "fast")
           (jq/show ($ :#cf-btn) "slow")
           (.val ($ :#file-chooser) "")))
