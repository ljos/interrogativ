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
             {:content (html [:h1 "Spørreundersøkelse"]
                             [:a {:href "#meny"
                                  :data-rel "dialog"
                                  :data-icon "gear"
                                  :data-iconpos "right"
                                  :data-transition "slidedown"
                                  :class "ui-btn-right"}
                              "Meny"])})
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
  (common/page  {:id "deg-selv"
    :header (common/header
             {:content (html [:h1 "Deg selv"]
                             [:a {:class "ui-btn-right"
                                  :href "#meny"
                                  :data-icon "gear"
                                  :data-iconpos "right"
                                  :data-rel "dialog"
                                  :data-transition "slidedown"}
                              " 1 / 4 "])})
    :content (common/content
              (common/radio-group
               {:name "P01Q01"
                :label [:h4 "1. Er du gutt eller jente?"]
                :groups ["Gutt" "Jente"]
                :type "horizontal"})
              (common/slider
               {:name "P01Q02"
                :label [:h4 "2. Hvor gammel er du?"]
                :value "16"
                :min "10"
                :max "25"})
              (common/select
               {:name "P01Q03"
                :label [:h4 "3. Hvor går du på skole?"]
                :values ["Sentrum"
                         "Årstad"
                         "Laksevåg"
                         "Fyllingsdalen"
                         "Fana"
                         "Ytrebygda"
                         "Åsane"
                         "Arna"
                         "Annet sted"]}))
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
                             [:a {:class "ui-btn-right"
                                  :href "#meny"
                                  :data-icon "gear"
                                  :data-iconpos "right"
                                  :data-rel "dialog"
                                  :data-transition "slidedown"}
                              " 2 / 4 "])})
    :content (common/content
                            (common/select
               {:name "P02Q04"
                :label [:h4 "4. Hva slags mobiltelefon har du?"]
                :values ["iPhone"
                         "Samsung"
                         "Sony"
                         "Nokia"
                         "Andre"]})
              (common/radio-table
               {:name "P02Q05"
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
              (common/radio-table
               {:name "P02Q06"
                :label [:h4 "6. Hvordan vil du helst skrive?"]
                :sections ["Tastatur"
                           "Touchskjerm"
                           "Håndskrift"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P02Q07"
                :label [:h4 "7. Hvordan vil du helst se levende bilder?"]
                :sections ["Kino"
                           "Hjemmekino"
                           "Flatskjerm-TV"
                           "Dataskjerm"
                           "iPad/Lesebrett"
                           "Mobilskjerm"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P02Q08"
                :label [:h4 (str "8. Liker du å forholde deg til flere skjermer samtidig,"
                                 "for eksempel at du både ser på TV og leser på mobilen?")]
                :values ["Liker det"
                         "Nøytral"
                         "Liker det ikke"]})
              (common/radio-table
               {:name "P02Q09"
                :label [:h4 "9. Hvordan vil du helst høre lyd?"]
                :sections ["Uten forsterkning"
                            "Ørepropper i mobil"
                           "Store øreklokker i mobil"
                           "Høytalere i rommet"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P02Q10"
                :label [:h4 "10. Hvordan vil du helst snakke?"]
                :sections ["Mobiltelefon"
                           "Mikrofon i videokamera"
                           "Mygg-mikrofon"
                           "Studiomikrofon"]
                :values ["&#x2b;" "&bull;" "&minus;"]}))
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
                             [:a {:class "ui-btn-right"
                                  :href "#meny"
                                  :data-icon "gear"
                                  :data-iconpos "right"
                                  :data-rel "dialog"
                                  :data-transition "slidedown"}
                              " 3 / 4 "])})
    :content (common/content
              (common/radio-table
               {:name "P03Q11"
                :label [:h4 (str "11. Hvilket geografisk område er du mest interessert i at "
                                 "skal bli dekket av mediene?")]
                :sections ["Skolen"
                           "Nærområde"
                           "Bergen"
                           "Hordaland"
                           "Norge"
                           "Verden"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03Q12"
                :label [:h4 "12. Hvilken type informasjon synes du er viktigst?"]
                :sections ["Nyheter"
                           "Musikk"
                           "Fakta"
                           "Fiksjon"
                           "Underholdning"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03Q13"
                :label [:h4 (str "13. Hvilken kanal vil du foretrekke til nyheter og andre "
                                 "offentlige saker?")]
                :sections ["Mobil"
                           "Webmedier"
                           "Fjernsyn"
                           "Papiravis"
                           "Radio"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03Q14"
                :label [:h4 (str "14. Hvor vil du helst være mens du bruker nyheter og "
                                 "annen journalistikk?")]
                :sections ["Hjemme"
                           "Reise"
                           "Skolen"
                           "Café/Klubb"
                           "Trening, tur"
                           "Stedet betyr ingenting"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-table
               {:name "P03Q15"
                :label [:h4 (str "15. Hvilke personer synes du bør være fremtredende "
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
                             [:a {:class "ui-btn-right"
                                  :href "#meny"
                                  :data-icon "gear"
                                  :data-iconpos "right"
                                  :data-rel "dialog"
                                  :data-transition "slidedown"}
                              " 4 / 4 "])})
    :content (common/content
              (common/radio-table
               {:name "P04Q16"
                :label [:h4 "16. Hvilken type medieproduksjon ønsker du selv å bruke tid på?"]
                :sections ["Snakke selv"
                           "Skrive og lese"
                           "Ta bilder"
                           "Filme video"
                           "Lage musikk"
                           "Programmere datamaskiner"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/radio-list
               {:name "P04Q17"
                :label [:h4 (str "17. Hvor mange ganger har du vært i avisen, på tv "
                                 "eller lignende?")]
                :values ["To eller flere"
                         "En gang"
                         "Aldri"]})
              (common/radio-table
               {:name "P04Q18"
                :label [:h4 (str "18. Hvilken mediekanal vil du sannsynligvis bruke hvis "
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
               {:name "P04Q19"
                :label [:h4 (str "19. Hvilke typer personlig informasjon er du villig "
                                 "til å dele med journalistiske offentligheten gjennom "
                                 "mobilen?")]
                :sections ["Dine bilder"
                           "Dine videoer"
                           "Din posisjon"
                           "Økonomiske forhold"
                           "Medisinsk informasjon"
                           "Sivilstatus"]
                :values ["&#x2b;" "&bull;" "&minus;"]})
              (common/textarea
               {:name "P04Q20"
                :label [:h4 (str "20. Har du noe du vil si? Vi tar det på største alvor "
                                 "i våre analyser.")]}))
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

(def meny
  (common/page
   {:id "meny"
    :header (common/header
             {:content [:h1 "Meny"]})
    :content  [:div {:data-role "content" :data-theme "c"}
               [:p {:id "menyp"}]]}))

(defpage "/" []
  (if (cookies/get :tracker)
    (redirect "/takk")
    (common/layout
     [:form {:action "/takk" :method "post"}
      forside
      deg-selv
      teknologi
      innhold
      deltagelse
      ferdig
      meny])))


