(ns interrogativ.models.spm
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.zip :as zip])
  (:import [java.io BufferedReader StringReader]
           [java.util.regex Pattern]))


(def page #"^(?s)^#[^#].*?(?=\n#[^#])")

(def document #"^(?s).*")

(def heading #"^##+.*")

(def question #"^(?s)?:.*(?=\m?:)")

(def paragrah #"(?s)^[^#?+-*].*(?=\n[\n#?+-*])")


(defn parse [spm]
  (loop [spm (BufferedReader. (StringReader. spm))
         schema {}]
    (if (empty? spm)
      schema
      )))