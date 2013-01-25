(ns interrogativ.views.takk
  (:require [interrogativ.models.data :as data]
            [interrogativ.views.mobile :as mobile]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))

(defpage [:post "/takk"] data
  (let [submitter-id (data/generate-submitter-id)]
    (cookies/put! :tracker {:value submitter-id :path "/" :max-age 86400})
    (data/store-answer (assoc (dissoc data :submitter)
                         :informant submitter-id))
    (redirect "/takk")))

(defpage "/takk" []
  (mobile/layout
   (mobile/page
    {:id "takk"
     :header (mobile/header
              {:content [:h1 " "]})
     :content (mobile/content
               [:h3 "Takk!"]
               [:p (str "Du har nå fylt ut alle fire seksjonene i undersøkelsen "
                        "”Hvilke nye medier trenger Norge mest”. "
                        "Ditt bidrag er verdifullt for forskerne "
                        "ved Institutt for informasjons- og medievitenskap. Vi vil "
                        "formidle resultatene i Bergens Tidende og/eller andre lokale "
                        "medier.")]
               [:p "Følg med!"]
               [:p "Hilsen" [:br]
                "Lars.Nyre@infomedia.uib.no"])
     :footer (mobile/footer
              {:id "takk-footer"
               :content [:h1 " "]})})))
