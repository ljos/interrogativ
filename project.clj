(defproject interrogativ "0.3.0"
  :description "Spørreskjema til 'Forskningsdagene UNG'"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.3.0-beta3"]
                 [noir-cljs "0.3.5"]
                 [org.clojure/tools.logging "0.2.3"]
                 [slingshot "0.10.3"]
                 [jayq "0.2.0"]
                 [crate "0.2.1"]
                 [org.clojure/clojurescript "0.0-1236"]]
  :main ^{:skip-aot true} interrogativ.server)
