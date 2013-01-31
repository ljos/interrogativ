(defproject interrogativ "0.6.1-SNAPSHOT"
  :dependencies [[jayq "2.0.0"]
                 [noir "1.3.0"]
  :description "Survey generator for the University of Bergen.'"
                 [prismatic/dommy "0.0.2-SNAPSHOT"]
                 [korma "0.3.0-RC2"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]}}
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-midje "2.0.1"]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds [{:source-paths ["cljs-src/editor"]
             :compiler
             {:output-to "resources/public/cljs/editor.js"
              :optimization :whitespace
              :pretty-print true
              :externs ["externs/jquery-1.8.js"]}}
            {:source-paths ["cljs-src/mobile"]
             :compiler
             {:output-to "resources/public/cljs/mobile.js"
              :optimization :whitespace
              :pretty-print true
              :externs ["externs/jquery-1.8.js"]}}
            {:source-paths ["cljs-src/upload"]
             :compiler
             {:output-to "resources/public/cljs/upload.js"
              :optimization :whitespace
              :pretty-print true
              :externs ["externs/jquery-1.8.js"]}}]}
  :main interrogativ.server)
