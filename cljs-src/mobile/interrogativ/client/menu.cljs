(ns interrogativ.client.menu
  (:require [jayq.core :as jq]
            [dommy.template :as template])
  (:use [jayq.core :only [$]]))

(defn html [& nodes]
  (apply str
         (map #(.-outerHTML (template/node %))
              nodes)))

(defn live [$elem events handler]
  (.live $elem (jq/->event events) handler))

(live ($ :#ferdig) :pagebeforeshow
  (fn [event]
    (jq/remove ($ "#ferdig div[role=heading]"))))

(live ($ :#meny) :pagebeforeshow
  (fn [event]
    (let [$menyp ($ :#menyp)]
      (jq/inner $menyp
                (html
                 [:h4 "Du kan avslutte når som helst ved å trykke på denne knappen."]
                 [:a {:href "#"
                      :id "avslutt"
                      :data-role "button"
                      :data-theme "c"}
                  "Avslutt"]
                 [:a {:href "#"
                      :data-rel "back"
                      :data-role "button"
                      :data-theme "a"}
                  "Tilbake"]))
      (jq/trigger $menyp "create")
      (jq/bind ($ :#avslutt) :click
        (fn []
          (jq/fade-out $menyp "fast"
            (fn []
              (jq/inner $menyp
                        (html
                         [:h4 "Er du sikker på at du vil avslutte?"]
                         [:a {:href "#ferdig"
                              :data-role "button"}
                          "Ja"]
                         [:a {:href "#"
                              :data-rel "back"
                              :data-role "button"}
                          "Nei"]))
              (jq/trigger $menyp :create)
              (jq/fade-in $menyp "slow"))))))))
