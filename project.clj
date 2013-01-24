(defproject interrogativ "0.3.5"
  :description "Spørreskjema til 'Forskningsdagene UNG'"
  :dependencies [[jayq "2.0.0"]
                 [noir "1.3.0"]
                 [noir-cljs "0.3.7"]
                 [org.clojure/clojure "1.4.0"]
                 [org.clojure/clojurescript "0.0-1450"]
                 [org.clojure/tools.logging "0.2.3"]]
  :exclusions [org.clojure/clojure]
  :main ^{:skip-aot true} interrogativ.server)
