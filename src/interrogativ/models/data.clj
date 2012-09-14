(ns interrogativ.models.data
  (:require [clojure.java.jdbc :as sql])
  (import [java util.Calendar
                text.SimpleDateFormat]))

(def today (.format (SimpleDateFormat. "yyyy-MM-dd")
                    (.getTime (Calendar/getInstance))))

(def ^:private submitters (atom #{}))

(def file-name (let [name (format "db/%s-interrogativ.db" today)]
                 (spit name "" :append true)
                 (doseq [submission (read-string (str "[" (slurp name) "]"))]
                   (swap! submitters conj (:submitter submission)))
                 name))

(defn- file-agent [file-name]
  (add-watch (agent nil) :file-writer
    (fn [key agent old new]
      (spit file-name new :append true))))

(defn- async-append  [file-agent content]
  (send file-agent (constantly content)))

(def ^:private store (file-agent file-name))

(defn store-answer [answer]
  (when-not (contains? submitters (:submitter answer))
    (swap! submitters conj (:submitter answer))
    (async-append store (str answer))))

(defn- random-string [length]
  (let [ascii-codes (concat (range 48 58)
                            (range 65 91))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn generate-submitter-id []
  (loop [submitter-id (random-string 16)]
    (if (contains? submitters submitter-id)
      (recur (random-string 16))
      submitter-id)))


