(ns interrogativ.server
  (:gen-class)
  (:require [noir.server :as server]
            [interrogativ.views.spm :as spm])
  (:import [java io.File]))

(server/load-views-ns 'interrogativ.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'interrogativ})
    (doseq [file (.listFiles (File. "qs/"))
            :when (not (.isDirectory file))]
      (spm/create-page-from (.getPath file)))))

