(ns interrogativ.client.menu
  (:require [jayq.core :as jq]
            [crate.core :as crate])
  (:use [jayq.core :only [$]]))

(defn quit []
  (let [html (crate/html
              [:h4 "Er du sikker på at du vil avslutte?"]
              [:a {:href "#ferdig"
                   :data-role "button"}
               "Ja"]
              [:a {:href "#"
                   :data-rel "back"
                   :data-role "button"}
               "No"])
        $menyp ($ :#menyp)]
    (jq/fade-out $menyp "fast"
      (fn []
        (jq/inner $menyp html)
        (jq/trigger $menyp :create)
        (jq/fade-in $menyp "slow")))))

(jq/delegate ($ :#ferdig) nil :pagebeforeshow 
  (fn []
    (jq/remove ($ :#ferdig "div[role=heading]"))))

(jq/delegate ($ :#meny) nil :pagebeforeshow
  (fn []
    (let [html (crate/html
                [:h4 "Du kan avslutte når som helst ved å trykke på"
                 "denne knappen."]
                [:a {:href "#"
                     :id "avslutt"
                     :data-role "button"
                     :data-theme "c"
                     :onclick "quit()"}
                 "Avslutt"]
                [:a {:href "#"
                     :data-rel "back"
                     :data-role "button"
                     :data-theme "a"}
                 "Tilbake"])
          $menyp ($ :#menyp)]
      (jq/inner $menyp html)
      (jq/trigger $menyp :create))))
