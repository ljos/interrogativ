(ns interrogativ.client.editor-save
  (:require [jayq.core :as jq])
  (:use [jayq.core :only [$]]))

(def editor (.edit js/ace "editor"))

(jq/bind ($ :#save) :click
  (fn []
    (jq/val ($ :#text)
            (-> editor
                .getSession
                .getValue))))
