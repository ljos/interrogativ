(ns interrogativ.test.views.login
  (:require [interrogativ.server :as server]
            [ring.adapter.jetty :refer [run-jetty]])
  (:use midje.sweet
        clj-webdriver.taxi))

(defonce server (run-jetty server/handler
                           {:port 8989
                            :join? false}))

(set-driver! {:browser :chrome} "http://localhost:8989/login")

(fact "Url should change after logging in with correct password.."
  (try
    (quick-fill-submit
     {"#uname" "test"}
     {"#pword" "test"}
     {"#submit" submit})
    (wait-until (= (current-url)
                   "http://localhost:8989/data")
                1000) ; Wait 1 second before giving up.
    (catch org.openqa.selenium.TimeoutException _
      nil))  => truthy)

(quit)

(.stop server)
