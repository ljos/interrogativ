(ns interrogativ.server
  (:require [clojure.java.io :as io]
            [interrogativ.models.spm :as spm]
            [noir.server :as server])
  (:gen-class))

(server/load-views-ns 'interrogativ.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'interrogativ})
    (doseq [file (.listFiles (io/file "qs/"))
            :when (not (.isDirectory file))]
      (spm/create-survey-from (.getPath file)))))
