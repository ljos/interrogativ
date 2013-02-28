(defproject interrogativ "0.10.1-SNAPSHOT"
  :description "Survey generator for the University of Bergen.'"
  :dependencies [[compojure "1.1.5" :exclusions [org.clojure/tools.macro]]
                 [jayq "2.0.0"]
                 [lib-noir "0.3.5" :exclusions [ring]]
                 [prismatic/dommy "0.0.3-SNAPSHOT"]
                 [korma "0.3.0-RC2"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [org.clojure/clojure "1.5.0-RC17"]
                 [com.taoensso/timbre "1.3.0"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]
                                  [clj-webdriver "0.6.0-beta2" :exclusions [[slingshot]
                                                                            cheshire]]
                                  [org.clojure/clojurescript "0.0-1576"]]}}
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-midje "2.0.1"]
            [lein-ring "0.8.2" :exclusions [[slingshot]
                                            [cheshire]
                                            org.clojure/clojure]]]
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
  :aot [interrogativ.parser.types
        interrogativ.models.parse
        interrogativ.parser.tokenize]
  :main interrogativ.server)
