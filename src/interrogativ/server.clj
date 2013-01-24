(ns interrogativ.server
  (:require [cljs.closure :as cljs]
            [clojure.java.io :as io]
            [interrogativ.models.spm :as spm]
            [noir.cljs.core :as client]
            [noir.server :as server])
  (:gen-class))

(server/load-views-ns 'interrogativ.views)

(defn create-options [page]
  (let [simple {:src-dir (str "cljs-src/" page "/")
                :output-to
                (str "resources/public/cljs/" page ".js") }
        advanced simple]
    {:simple simple
     :advanced advanced}))

(def options-all (let [simple {:src-dir "cljs-src"}
                       advanced simple]
                   {:simple simple
                    :advanced advanced }))
(def options-editor (create-options "editor"))
(def options-mobile (create-options "mobile"))
(def options-upload (create-options "upload"))

(defn compile-options [option m]
  (merge {:output-dir "resources/public/cljs/"
          :output-to "resources/public/cljs/bootstrap.js"
          :src-dir "src/"
          :optimizations :simple
          :pretty-print true}
         (option m)))

(defn build [option m]
  (let [options (compile-options option m)]
    (cljs/build (:src-dir options) options)))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))
        option (if (= :dev mode) :simple :advanced)]
    (build option options-all)
    (build option options-editor)
    (build option options-mobile)
    (build option options-upload)
    (server/start port {:mode mode
                        :ns 'interrogativ})
    (doseq [file (.listFiles (io/file "qs/"))
            :when (not (.isDirectory file))]
      (spm/create-page-from (.getPath file)))))

