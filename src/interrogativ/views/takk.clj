(ns interrogativ.views.takk
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))

(defpage [:post "/takk"] data
  (let [submitter-id (data/generate-submitter-id)]
    (cookies/put! :tracker {:value submitter-id :path "/" :expires 1 :max-age 86400})
    (data/store-answer (assoc (dissoc data :submitter)
                         :informant submitter-id))
    (redirect "/takk")))

(defpage "/takk" []
  (common/layout
   (common/page
    {:id "takk"
     :header (common/header
              {:content [:h1 " "]})
     :content (common/content
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
     :footer (common/footer
              {:id "takk-footer"
               :content [:h1 " "]})})))