(ns interrogativ.models.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [interrogativ.db.connection :as db]))

(defn survey-for-name [name]
  (db/html-for-survey name))

(defn thankyou-for-name [name]
  (db/thankyou-for-survey name))

(defn store-answer
  "store the answer given to the survey"
  [page answer]
  (db/insert-answer (:informant answer) page answer))

(def submitter-id (atom 10000))
(defn new-submitter-id!
  "returns a new submitter-id"
  []
  (Long/toString (swap! submitter-id inc)))

(defn create-csv-for-page 
  "creates a csv for the answers given to survey on page"
  [page]
  (let [submissions (db/answers-for-page page)
        keys (into (sorted-set) (mapcat keys submissions))]
    (log/info "Create csv for page: " page)
    (with-out-str 
      (println (str/join "," (map (partial format "\"%s\"") keys)))
      (doseq [submission submissions]
        (println (str/join ","
                           (map (partial format "\"%s\"")
                                (for [key keys]
                                  (get submission key -1)))))))))

(defn markdown-for-page 
  "retrives the markdown from the database"
  [page]
  (db/markdown-for-survey page))

(defn upload-file [{:keys [filename tempfile]}]
  (log/info (format "Storing file qs/%s" filename))
  (db/insert-survey (str/replace filename #"\..*$" "")
                    (slurp tempfile)))

(defn pages []
  (map (partial str "/qs/")
       (db/select-all-pages)))
