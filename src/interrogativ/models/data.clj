(ns interrogativ.models.data
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.logging :as log])
  (:import [java io.File
                 util.Calendar
                 text.SimpleDateFormat]
           [org.apache.commons.io FileUtils]))

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

(defn date []
  (let [date (.format (SimpleDateFormat. "yyyy-MM-dd")
                      (.getTime (Calendar/getInstance)))]
    (log/info "Current date is: " date)
    date))

(defn- page-agent [page]
  (add-watch (agent nil) :file-writer
             (fn [key agent old new]
               (let [file-name (format "db/%s/%s.dat" page (date))]
                 (log/info "Store to file " file-name
                           " new answer:" new)
                 (spit file-name new :append true)))))

(def domains (atom {}))

(defn create-store [page]
  (log/info "Create store for page: " page)
  (let [page-key (keyword page)]
    (-> (str "db/" page) File. .mkdirs)
    (swap! domains
      assoc-in [page-key :store]
      (page-agent page))
    (swap! domains
      assoc-in [page-key :submits]
      (let [file (File. (str "db" page "/" (date) ".dat"))]
        (if (.exists file)
          (into (sorted-set)
                (map :informant
                     (read-string (str "["
                                       (slurp file)
                                       "]"))))
         (sorted-set))))))

(defn store-answer
  ([answer]
     (when-not (contains? submitters (:informant answer))
       (swap! submitters conj (:informant answer))
       (async-append store (str answer "\n"))))
  ([answer page]
     (let [page-key (keyword page)]
       (when-not (contains? (get-in @domains [page-key :submits])
                            (:informant answer))
         (swap! domains
           update-in [page-key :submits]
             conj (:informant answer))
         (async-append (get-in @domains [page-key :store])
                       (str answer "\n"))))))

(defn- random-string [length]
  (let [ascii-codes (concat (range 48 58)
                            (range 65 91))]
    (apply str (repeatedly length #(char (rand-nth ascii-codes))))))

(defn generate-submitter-id
  ([]
     (loop [submitter-id (random-string 16)]
       (if (contains? submitters submitter-id)
         (recur (random-string 16))
         (do (log/info "Created new id: " submitter-id)
             submitter-id))))
  ([page]
     (loop [submitter-id (random-string 16)]
       (if (contains? (get-in @domains [(keyword page) :submits])
                      submitter-id)
         (recur (random-string 16))
         (do (log/info "Create new id: " submitter-id
                       " for page: " page)
             submitter-id)))))

(defn create-csv-from-file [file-name]
  (let [submissions (read-string
                     (str "["
                          (string/replace (slurp file-name)
                                          #"(?imu)[øåæé,/]" "")
                          "]"))
        keys (into (sorted-set) (mapcat keys submissions))]
    (log/info "Create csv from file: " file-name)
    (with-out-str 
      (println (string/join "," (map name keys)))
      (doseq [submission submissions]
        (println (string/join ","
                              (map (partial format "\"%s\"")
                                   (for [key keys]
                                     (get submission key -1)))))))))

(defn create-csvs-for-page [page]
  (log/info "Create csv's for page: " page)
  (for [file (.listFiles (File. (format "db/%s" page)))
        :when (not (.isDirectory file))]
    (create-csv-from-file file)))

(defn upload-file [{:keys [filename tempfile]}]
  (log/info (format "Storing file qs/%s" filename))
  (FileUtils/copyFile tempfile
                      (File. (str "qs/" filename))))
