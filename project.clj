(defproject interrogativ "0.9.0-SNAPSHOT"
  :description "Survey generator for the University of Bergen.'"
  :dependencies [[compojure "1.1.5"]
                 [jayq "2.0.0"]
                 [lib-noir "0.3.5"]
                 [prismatic/dommy "0.0.2-SNAPSHOT"]
                 [korma "0.3.0-RC2"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.logging "0.2.3"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]}}
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-midje "2.0.1"]
            [lein-ring "0.8.2"]]
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
  :ring {:handler interrogativ.server/handler}
  :main interrogativ.server)
