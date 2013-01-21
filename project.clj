(defproject interrogativ "0.3.0"
  :description "Spørreskjema til 'Forskningsdagene UNG'"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]
                 [noir-cljs "0.3.7"]
                 [org.clojure/tools.logging "0.2.3"]
                 [slingshot "0.10.3"]
                 [jayq "0.2.0"]
                 [crate "0.2.1"]
                 [org.clojure/clojurescript "0.0-1450"]]
  :exclusions [org.clojure/clojure]
  :main ^{:skip-aot true} interrogativ.server)
