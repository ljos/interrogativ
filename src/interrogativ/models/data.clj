(ns interrogativ.models.data
  (require [clojure.java.io :as io]
           [clojure.string :as string])
  (import [java util.Calendar
                text.SimpleDateFormat]))

(def today (.format (SimpleDateFormat. "yyyy-MM-dd")
                    (.getTime (Calendar/getInstance))))

(def ^:private submitters (atom (sorted-set)))

(def ^:private file-name
  (let [name (format "db/%s-interrogativ.dat" today)]
    (spit name "" :append true)
    (doseq [submission (read-string (str "[" (slurp name) "]"))]
      (swap! submitters conj (:informant submission)))
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
    (async-append store (str answer "\n"))))

(defn- random-string [length]
  (let [ascii-codes (concat (range 48 58)
                            (range 65 91))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn generate-submitter-id []
  (loop [submitter-id (random-string 16)]
    (if (contains? submitters submitter-id)
      (recur (random-string 16))
      submitter-id)))

(defn create-csv-from-file [file-name]
  (let [submissions (read-string (str "[" (slurp file-name) "]"))
        keys (into (sorted-set) (mapcat keys submissions))]
    (with-out-str 
      (println (string/join "," (map name keys)))
      (doseq [submission submissions]
        (println (string/join ","
                              (map (partial format "\"%s\"")
                                   (for [key keys]
                                     (get submission key -1)))))))))


