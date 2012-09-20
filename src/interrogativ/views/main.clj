(ns interrogativ.views.main
  (:require [interrogativ.views.common :as common]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))

(def forside
  (common/page
   {:id "forside"
    :header (common/header
             {:content [:h1 "Spørreundersøkelse"]})
    :content (common/content
              [:h3 "Hvilke nye medier trenger Norge mest?"]
              [:p (str "Hvis du skulle tenke deg et helt nytt og annerledes medium; "
                       "hvordan skulle det være? Er det riktig at ingen ungdommer liker "
                       "papirmedier? Bør man kunne dele nakenbilder på nett?")]
              [:p (str "I denne undersøkelsen ber vi deg svære så ærlig du kan på "
                       "spørsmål om hvordan medier bør være. Kanskje kan vi finne ideer "
                       "til et radikalt nytt medium?")]
              [:p (str "Ved å trykke på knappen ”Levér skjema” gir du samtidig ditt "
                       "samtykke til at opplysningene du har fylt ut kan bli brukt til "
                       "forskning og formidling. Din telefon blir skilt ut fra de andre "
                       "ved at vi lager en unik id pr. telefon, men nummeret eller annen "
                       "sensitiv informasjon blir ikke lagret.")]
              [:p "Start undersøkelsen ved å trykke videre."])
    :footer (common/footer
             {:content (common/grid-b
                        {:block-c (common/right-button
                                   {:link "#deg-selv"
                                    :label "Neste"
                                    :inline "false"})})})}))

(def deg-selv
  (common/page
   {:id "deg-selv"
    :header (common/header
             {:content [:h1 "Deg selv"]})
    :content (common/content
              (common/radio-group
               {:name "P01-degselv-Q01"
                :label "Er du gutt eller jente?"
                :groups ["Gutt" "Jente"]
                :type "horizontal"})
              (common/slider
               {:label "Hvor gammel er du?"
                :name "P01-degselv-Q02"
                :value "16"
                :min "10"
                :max "25"})
              (common/select
               {:name "P01-degselv-Q03"
                :label "Hvor går du på skole?"
                :values ["Sentrum"
                         "Årstad"
                         "Laksevåg"
                         "Fyllingsdalen"
                         "Fana"
                         "Ytrebygda"
                         "Åsane"
                         "Arna"
                         "Annet sted"]})
              (common/select
               {:name "P01-degselv-Q03"
                :label "Hvilken studieretning tar du?"
                :values ["Realfag"
                         "Samfunnsfag"
                         "Språk"
                         "Økonomi"
                         "Musikk"
                         "Dans"
                         "Drama"
                         "Idrettsfag"
                         "Bygg- og anleggsteknikk"
                         "Design og håndverk"
                         "Elektrofag"
                         "Helse- og sosialfag"
                         "Medier og kommunikasjon"
                         "Naturbruk"
                         "Restaurant- og matfag"
                         "Service og samferdsel"
                         "eknikk og industriell produksjon"]}))
    :footer (common/footer
             {:content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#forside"
                                    :label "ilbake"
                                    :inline "false"})
                         :block-c (common/right-button
                                   {:link "#teknologi"
                                    :label "Neste"
                                    :inline "false"})})})}))

(def teknologi
  (common/page
   {:id "teknologi"
    :header (common/header
             {:content [:h1 "Teknologi"]})
    :content (common/content
              (common/radio-table
               {:name "P02-teknologi-Q01"
                :label "Hvilke medieplatformer liker du best?"
                :sections ["Mobil"
                           "Bærbar datamaskin"
                           "Spillmaskiner"
                           "Lesebrett"
                           "TV"
                           "Musikkspiller"
                           "Radio"
                           "Papir"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/select
               {:name "P02-teknologi-Q02"
                :label "Hva slags mobiltelefon har du?"
                :values ["iPhone"
                         "Samsung"
                         "Sony-Ericsson"
                         "Nokia"
                         "Andre"]})
              (common/radio-table
               {:name "P02-teknologi-Q03"
                :label "Hvordan vil du helst skrive?"
                :sections ["Tastatur"
                           "Touchskjerm"
                           "Håndskrift"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P02-teknologi-Q04"
                :label "Hvordan vil du helst se levende bilder?"
                :sections ["Kino"
                           "Hjemmekino"
                           "Flatskjerm-TV"
                           "Dataskjerm"
                           "Lesebrett"
                           "Mobilskjerm"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P02-teknologi-Q05"
                :label (str "Liker du å forholde deg til flere skjermer samtidig,"
                            "for eksempel at du både ser på TV og leser på mobil?")
                :values ["Liker det"
                         "Nøytral"
                         "Liker det ikke"]})
              (common/radio-table
               {:name "P02-teknologi-Q06"
                :label "Hvordan vil du helst høre lyd?"
                :sections ["Høretelefoner"
                           "Høytalere"
                           "Ingen preferanse"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P02-teknologi-Q07"
                :label "Hvordan vil du helst snakke?"
                :sections ["Stor mikrofon"
                           "Mygg-mikrofon"
                           "Mikk i videokamera"
                           "Mobiltelefon"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P02-teknologi-Q08"
                :label (str "Hvor ofte tenker du på at din posisjon faktisk blir registrert "
                            "hele tiden, og kan brukes av ulike firma som Facebook, Google, "
                            "etc?")
                :values ["Svært ofte"
                         "Ofte"
                         "Noe"
                         "Sjelden"
                         "Svært sjelden"]})
              (common/radio-list
               {:name "P02-teknologi-Q09"
                :label (str "Hvor ofte tenker du på at din posisjon faktisk blir registrert "
                            "hele tiden, og kan brukes av ulike firma som Facebook, Google, "
                            "etc?")
                :values ["Svært ofte"
                         "Ofte"
                         "Noe"
                         "Sjelden"
                         "Svært sjelden"]}))
    :footer (common/footer
             {:content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#deg-selv"
                                    :inline "false"
                                    :label "Tilbake"})
                         :block-c (common/right-button
                                   {:link "#innhold"
                                    :inline "false"
                                    :label "Neste"})})})}))

(def innhold
  (common/page
   {:id "innhold"
    :header (common/header
             {:content [:h1 "Innhold"]})
    :content (common/content
              (common/radio-table
               {:name "P03-innhold-Q01"
                :label (str "Hvilket geografisk område er du mest interessert i at "
                            "skal bli dekket av mediene?")
                :sections ["Skolen"
                           "Nærområde"
                           "Bergen"
                           "Hordaland"
                           "Norge"
                           "Verden"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03-innhold-Q02"
                :label "Hvilken type informasjon er viktigst?"
                :sections ["Nyheter"
                           "Musikk"
                           "Dokumentar"
                           "Fiksjon"
                           "Fotografier"
                           "Underholdning"
                           "Reality-TV"
                           "Facebook"
                           "Nakenbilder"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03-innhold-Q03"
                :label (str "Hvilken mediekanal vil du foretrekke til saker om offentlige "
                            "forhold?")
                :sections ["Papiravis"
                           "Webavis"
                           "Radio"
                           "Fjernsyn"
                           "Film"
                           "Mobil"
                           "YouTube"
                           "Ukeblader"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03-innhold-Q04"
                :label (str "Hvor vil du helst være mens du bruker nyheter og "
                            "annen journalistikk?")
                :sections ["Hjemme"
                           "Reise"
                           "Skolen"
                           "Café/Klubb"
                           "Trening, tur"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03-innhold-Q05"
                :label  "Hvilke kilder synes du bør være fremtredende i offentligheten?"
                :sections ["Forskere"
                           "Kjendiser"
                           "Journalister"
                           "Politikere"
                           "Statlige funksjonærer"
                           "Forretningsfolk"
                           "Lærere"
                           "Vanlige voksne"
                           "Ungdom"
                           "Barn"]
                :values ["&#x2b;" "&bull;" "&minus;"]}))
    :footer (common/footer
             {:content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#teknologi"
                                    :inline "false"
                                    :label "Tilbake"})
                         :block-c (common/right-button
                                   {:link "#deltagelse"
                                    :inline "false"
                                    :label "Neste"})})})}))

(def deltagelse
  (common/page
   {:id "deltagelse"
    :header (common/header
             {:content [:h1 "Deltakelse"]})
    :content (common/content
              (common/radio-table
               {:name "P04-deltagelse-Q01"
                :label "Hvem ønsker du mest å kommuniserer med på telefonen?"
                :sections ["Venner"
                           "Søsken"
                           "Mor"
                           "Far"
                           "Annen familie"
                           "Andre voksne"
                           "Lærere"
                           "Medelever"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P04-deltagelse-Q02"
                :label "Hvilken medieproduksjon ønsker du mest å bruke tid på?"
                :sections ["Snakke"
                           "Skrive"
                           "Ta bilder"
                           "Ta video"
                           "Lage musikk"
                           "Programmere"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P04-deltagelse-Q03"
                :label (str "Har du vært i avisen, på tv, radio, nettavis eller"
                            "andre journalistiske medier?")
                :values ["To eller flere"
                         "En gang"
                         "Aldri"]})
              (common/radio-table
               {:name "P04-deltagelse-Q04"
                :label (str "Hvilken mediekanal vil du sannsynligvis bruke hvis "
                            "du skal bidra med et innspill til offentligheten?")
                :sections ["Papirbrev"
                           "Telefonsamtale"
                           "SMS"
                           "Epost"
                           "Video"
                           "Kommentar i nettaviser"
                           "Blogg"
                           "Sosialt medium"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P04-deltagelse-Q05"
                :label (str "Hvilke typer personlig informasjon er du villig til å dele "
                            "med journalistiske offentligheten gjennom mobilen?")
                :sections ["Egne bilder"
                           "Egne vidoer"
                           "Din posisjon"
                           "Kommentarer"
                           "Interssante ting"
                           "Opplysninger om familien"
                           "Nyheter fra skolen"
                           "Nyheter om interesser"
                           "Din økonomi"
                           "Din helse"
                           "Alder"
                           "Bosted"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P04-deltagelse-Q06"
                :label (str "Er det greit at mediefirma bruker din personlige"
                            "informasjon til journalistiske formål, for eksempel"
                            "lege en raportasje med tekst og bilder?")
                :values ["Helt greit"
                         "Greit"
                         "Nøytral"
                         "Ugreit"
                         "Svært ugreit"]}))
    :footer (common/footer
             {:content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#innhold"
                                    :inline "false"
                                    :label "Tilbake"})
                         :block-c (common/right-button
                                   {:link "#ferdig"
                                    :inline "false"
                                    :label "Neste"})})})}))

(def ferdig
  (common/page
   {:id "ferdig"
    :header (common/header
             {:content [:h1 "Spørreundersøkelse"]})
    :content (common/content
              [:h3 "Ferdig"]
              [:p (str "Ved å trykke på knappen ”Levér” gir du samtidig ditt samtykke "
                       "til at opplysningene du har fylt ut kan bli brukt til forskning "
                       "og formidling. Ditt svar blir skilt ut ved at vi har automatiskt "
                       "tildelt mobilen din en unik id, men nummeret ditt eller annen "
                       "personlig informasjon blir ikke lagret.")])
    :footer (common/footer
             {
              :content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#deltagelse"
                                    :inline "false"
                                    :label "Tilbake"})
                         :block-c [:input {:data-icon "arrow-r"
                                           :data-iconpos "right"
                                           :data-inline "false"
                                           :data-mini "true"
                                           :type "submit"
                                           :name "submitter"
                                           :value "Levér"}]})})}))

(defpage "/" []
 ;if (cookies/get :tracker)
 ;(redirect "/takk")
  (common/layout
   [:form {:action "/takk" :method "post"}
    forside
    deg-selv
    teknologi
    innhold
    deltagelse
    ferdig]))

(defpage [:post "/takk"] data
  (let [submitter-id (data/generate-submitter-id)]
    (cookies/put! :tracker {:value submitter-id :path "/" :expires 1 :max-age 86400})
    (data/store-answer (assoc (dissoc data :submitter) :informant submitter-id))
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
               [:p "Følg med!"])
     :footer (common/footer
              {:content [:h1 " "]})})))
