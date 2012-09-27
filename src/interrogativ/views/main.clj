(ns interrogativ.views.main
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]
        [hiccup.core :only [html]]))

(def forside
  (common/page
   {:id "forside"
    :header (common/header
             {:content [:h1 "Spørreundersøkelse"]})
    :content (common/content
              [:h3 "Hvilke nye medier trenger Norge mest?"]
              [:p (str "Hvis du skulle tenke deg et helt nytt og annerledes medium; "
                       "hvordan skulle det være?")]
              [:p (str "I denne undersøkelsen ber vi deg svare så ærlig du kan på "
                       "spørsmål om hvordan medier bør være i framtiden. Vi ønsker "
                       "å bygge et helt nytt medium og trenger din hjelp.")]
              [:p (str "Det er frivillig å delta. Ved å trykke på knappen ”Levér "
                        "skjema” (helt til slutt) gir du ditt "
                       "samtykke til at opplysningene kan bli brukt til "
                       "forskning og formidling.")]
              [:p "Start undersøkelsen ved å trykke på \"Neste\"."]
              [:br]
              [:p [:b "Ansvarlig:"] [:br]
               "Professor Lars Nyre" [:br]
               "Institutt for informasjons- og medievitenskap" [:br]
               "Universitetet i Bergen" [:br]
               "Epost: Lars.Nyre@infomedia.uib.no"]
              [:p [:b "Web:"] [:br]
               "Bjarte Johansen" [:br]
               "Epost: bjo013@uib.no"])
    :footer (common/footer
             {:id "forside-footer"
              :content (common/grid-b
                        {:block-c (common/right-button
                                   {:link "#deg-selv"
                                    :label "Neste" 
                                    :inline "false"})})})}))

(def deg-selv
  (common/page
   {:id "deg-selv"
    :header (common/header
             {:content (html [:h1 "Deg selv"]
                             [:a {:class "ui-btn-right"} " 1 / 4 "])})
    :content (common/content
              (common/radio-group
               {:name "P01degselvQ01"
                :label [:h4 "1. Er du gutt eller jente?"]
                :groups ["Gutt" "Jente"]
                :type "horizontal"})
              (common/slider
               {:name "P01degselvQ02"
                :label [:h4 "2. Hvor gammel er du?"]
                :value "16"
                :min "10"
                :max "25"})
              (common/select
               {:name "P01degselvQ03"
                :label [:h4 "3. Hvor går du på skole?"]
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
               {:name "P01degselvQ04"
                :label [:h4 "4. Hvilken studieretning tar du?"]
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
                         "Teknikk og industriell produksjon"]}))
    :footer (common/footer
             {:id "degselv-footer"
              :content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#forside"
                                    :label "Tilbake"
                                    :inline "false"})
                         :block-c (common/right-button
                                   {:link "#teknologi"
                                    :label "Neste"
                                    :inline "false"})})})}))

(def teknologi
  (common/page
   {:id "teknologi"
    :header (common/header
             {:content (html [:h1 "Teknologi"]
                             [:a {:class "ui-btn-right"} " 2 / 4 "])})
    :content (common/content
              (common/radio-table
               {:name "P02teknologiQ01"
                :label [:h4 "5. Hvilke medieplatformer liker du best?"]
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
               {:name "P02teknologiQ02"
                :label [:h4 "6. Hva slags mobiltelefon har du?"]
                :values ["iPhone"
                         "Samsung"
                         "Sony-Ericsson"
                         "Nokia"
                         "Andre"]})
              (common/radio-table
               {:name "P02teknologiQ03"
                :label [:h4 "7. Hvordan vil du helst skrive?"]
                :sections ["Tastatur"
                           "Touchskjerm"
                           "Håndskrift"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P02teknologiQ04"
                :label [:h4 "8. Hvordan vil du helst se levende bilder?"]
                :sections ["Kino"
                           "Hjemmekino"
                           "Flatskjerm-TV"
                           "Dataskjerm"
                           "iPad/Lesebrett"
                           "Mobilskjerm"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P02teknologiQ05"
                :label [:h4 (str "9. Liker du å forholde deg til flere skjermer samtidig,"
                                 "for eksempel at du både ser på TV og leser på mobil?")]
                :values ["Liker det"
                         "Nøytral"
                         "Liker det ikke"]})
              (common/radio-table
               {:name "P02teknologiQ06"
                :label [:h4 "10. Hvordan vil du helst høre lyd?"]
                :sections ["Uten forsterkning"
                            "Ørepropper i mobil"
                           "Store øreklokker i mobil"
                           "Høytalere i rommet"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P02teknologiQ07"
                :label [:h4 "11. Hvordan vil du helst snakke?"]
                :sections ["Mobiltelefon"
                           "Mikrofon i videokamera"
                           "Mygg-mikrofon"
                           "Studiomikrofon"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P02teknologiQ08"
                :label [:h4 (str "12. Hvor ofte tenker du på at din posisjon faktisk blir "
                             "registrert hele tiden, og kan brukes av ulike firma som "
                             " Facebook, Google, etc?")]
                :values ["Svært ofte"
                         "Ofte"
                         "Noe"
                         "Sjelden"
                         "Svært sjelden"]}))
    :footer (common/footer
             {:id "teknologi-footer"
              :content (common/grid-b
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
             {:content (html [:h1 "Innhold"]
                             [:a {:class "ui-btn-right"} " 3 / 4 "])})
    :content (common/content
              (common/radio-table
               {:name "P03innholdQ01"
                :label [:h4 (str "13. Hvilket geografisk område er du mest interessert i at "
                                 "skal bli dekket av mediene?")]
                :sections ["Skolen"
                           "Nærområde"
                           "Bergen"
                           "Hordaland"
                           "Norge"
                           "Verden"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03innholdQ02"
                :label [:h4 "14. Hvilken type informasjon er viktigst?"]
                :sections ["Nyheter"
                           "Musikk"
                           "Fakta"
                           "Fiksjon"
                           "Underholdning"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03innholdQ03"
                :label [:h4 (str "15. Hvilken kanal vil du foretrekke til nyheter og andre"
                                 "offentlige saker?")]
                :sections ["Mobil"
                           "Webmedier"
                           "Fjernsyn"
                           "Papiravis"
                           "Radio"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03innholdQ04"
                :label [:h4 (str "16. Hvor vil du helst være mens du bruker nyheter og "
                                 "annen journalistikk?")]
                :sections ["Hjemme"
                           "Reise"
                           "Skolen"
                           "Café/Klubb"
                           "Trening, tur"
                           "Stedet betyr ingenting"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03innholdQ05"
                :label  [:h4 (str "17. Hvilke personer synes du bør være fremtredende "
                                  "i offentligheten?")]
                :sections ["Kjendiser"
                           "Journalister"
                           "Politikere"
                           "Forskere"
                           "Forretningsfolk"
                           "Lærere"
                           "Vanlige voksne"
                           "Ungdom"]
                :values ["&#x2b;" "&bull;" "&minus;"]}))
    :footer (common/footer
             {:id "innhold-footer"
              :content (common/grid-b
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
             {:content (html [:h1 "Deltakelse"]
                             [:a {:class "ui-btn-right"} " 4 / 4 "])})
    :content (common/content
              (common/radio-table
               {:name "P04deltagelseQ01"
                :label [:h4 "18. Hvem ønsker du mest å kommuniserer med på telefonen?"]
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
               {:name "P04deltagelseQ02"
                :label [:h4 "19. Hvilken type medieproduksjon ønsker du selv å bruke tid på?"]
                :sections ["Snakke selv"
                           "Skrive og lese"
                           "Ta bilder"
                           "Filme video"
                           "Lage musikk"
                           "Programmere datamaskiner"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P04deltagelseQ03"
                :label [:h4 (str "20. Hvor mange ganger har du vært i avisen, på tv "
                                 "eller lignende?")]
                :values ["To eller flere"
                         "En gang"
                         "Aldri"]})
              (common/radio-table
               {:name "P04deltagelseQ04"
                :label [:h4 (str "21. Hvilken mediekanal vil du sannsynligvis bruke hvis "
                                 "du skal bidra med et innspill til offentligheten?")]
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
               {:name "P04deltagelseQ05"
                :label [:h4 (str "22. Hvilke typer personlig informasjon er du villig "
                                 "til å dele med journalistiske offentligheten gjennom "
                                 "mobilen?")]
                :sections ["Dine bilder"
                           "Dine  videoer"
                           "Din posisjon"
                           "Økonomiske forhold"
                           "Medisinsk informasjon"
                           "Sivilstatus"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P04deltagelseQ06"
                :label [:h4 (str "23. Er det greit at mediefirma bruker din personlige"
                                 "informasjon til journalistiske formål, for eksempel"
                                 "lage en raportasje med tekst og bilder?")]
                :values ["Helt greit"
                         "Greit"
                         "Nøytral"
                         "Ugreit"
                         "Svært ugreit"]}))
    :footer (common/footer
             {:id "deltagelse-footer"
              :content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#innhold"
                                    :inline "false"
                                    :id "tilbakeinnhold"
                                    :label "Tilbake"})
                         :block-c (common/right-button
                                   {:link "#ferdig"
                                    :id "tilferdig"
                                    :inline "false"
                                    :label "Neste"})})})}))

(def ferdig
  (common/page
   {:id "ferdig"
    :header (common/header
             {:content [:h1 "Spørreundersøkelse"]})
    :content (common/content
              [:div {:class "ikkeferdig"}]
              [:h2 "Ferdig"]
              [:p (str "Ved å trykke på knappen ”Levér” gir du samtidig ditt samtykke "
                       "til at opplysningene du har fylt ut kan bli brukt til forskning "
                       "og formidling. Ditt svar blir skilt ut ved at vi har automatiskt "
                       "tildelt svaret en unik id, men nummeret fra telefonen eller "
                       "annen personlig informasjon blir ikke lagret.")])
    :footer (common/footer
             {:id "ferdig-footer"
              :content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#deltagelse"
                                    :id "tildeltagelse"
                                    :inline "false"
                                    :label "Tilbake"})
                         ;; Input ends the form defined in "/"
                         :block-c [:input {:data-icon "arrow-r"
                                           :data-iconpos "right"
                                           :data-inline "false"
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


