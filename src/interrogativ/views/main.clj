(ns interrogativ.views.main
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpartial defpage]]
        [noir.response :only [redirect]]
        [hiccup.page :only [html5]]))

(def forside
  (common/page
   {:id "forside"
    :header (common/header
             {:content [:h1 "Spørreundersøkelse"]})
    :content (common/content
              [:h3 "Hvilket nytt medium trenger Norge mest?"]
              [:p (str "Hvis du skulle tenke deg et helt nytt og annerledes medium; "
                       "hvordan skulle det være? Er det riktig at ingen ungdommer liker "
                       "papirmedier? Hva mener du om deling av nakenbilder?")]
              [:p (str "I denne undersøkelsen ber vi deg svære så ærlig du kan på "
                       "spørsmål om hvordan medier bør være.")]
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
               {:name "kjønn"
                :label "Er du gutt eller jente?"
                :groups ["Gutt" "Jente"]
                :type "horizontal"})
              (common/slider
               {:label "Hvor gammel er du?"
                :name "alder"
                :id "alder"
                :value "16"
                :min "10"
                :max "25"})
              (common/select
               {:name "bosted"
                :label "Hvor bor du?"
                :values ["Sentrum"
                         "Årstad"
                         "Laksevåg"
                         "Fyllingsdalen"
                         "Fana"
                         "Ytrebygda"
                         "Åsane"
                         "Arna"]})
              (common/select
               {:name "studieretning"
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
                         "Teknikk og industriell produksjon"]})
              (common/textarea
               {:label "Hva tror du at du gjør om ti år? Skriv selv:"
                :name "10år"}))
    :footer (common/footer
             {:content (common/grid-b
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
             {:content [:h1 "Teknologi"]})
    :content (common/content
              (common/select
               {:name "mobil"
                :label "Hva slags mobiltelefon har du?"
                :values ["iPhone"
                         "Samsung"
                         "Sony-Ericsson"
                         "Nokia"
                         "Andre"]})
              (common/radio-list
               {:name "posisjon"
                :label (str "Hvor ofte tenker du på at din posisjon faktisk blir registrert "
                            "hele tiden, og kan brukes av ulike firma som Facebook, Google, "
                            "etc?")
                :values ["Svært ofte"
                         "Ofte"
                         "Noe"
                         "Sjelden"
                         "Svært sjelden"]})
              (common/checkbox-list
               {:name "medier-rom"
                :label "Hvilke medier har du (på rommet, og/eller med familien)?"
                :values ["Mobil"
                         "Bærbar datamaskin (PC eller Mac)"
                         "X-box, Playstation, el.lignende"
                         "Lesebrett"
                         "TV"
                         "Musikkspiller (iPod, mp3-spiller)"
                         "Radio"]})
              (common/radio-list
               {:name "skjermer"
                :label (str "Er du vant med å se på to eller flere skjermer samtidig, "
                            "for eksempel at du både ser på TV og leser på mobilen?")
                :values ["Svært vanlig"
                         "Vanlig"
                         "Noe"
                         "Uvanlig"
                         "Svært uvanlig"]}))
    :footer (common/footer
             {:content (common/grid-b
                        {:block-a (common/left-button
                                   {:link "#deg-selv"
                                    :inline "false"
                                    :label "Tilbake"})
                         :block-c (common/right-button
                                   {:link "#sjangre-og-innhold"
                                    :inline "false"
                                    :label "Neste"})})})}))

(def sjangre-og-innhold
  (common/page
   {:id "sjangre-og-innhold"
    :header (common/header
             {:content [:h1 "Sjangre og innhold"]})
    :content (common/content
              (common/radio-table
               {:name "mediekanal"
                :label (str "Hvilken mediekanal vil du foretrekke til saker om offentlige "
                            "forhold, regjering og slikt?")
                :sections ["Papiravis"
                           "Radio"
                           "Fjernsyn"
                           "Film"
                           "Dataspill"
                           "Mobil"
                           "YouTube"
                           "Musikk"
                           "Ukeblader"
                           "Livekonsert"
                           "MER?"]
                :values ["Godt"
                         "Nøytral"
                         "Dårlig"]})
              (common/radio-table
               {:name "informasjon"
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
                :values ["Svært"
                         "Noe"
                         "Ikke"]})
              (common/radio-list
               {:name "område"
                :label (str "Hvilket område er du mest opptatt av når det gjelder nyheter "
                            "og annen informasjon?")
                :values ["Skolen/nærområdet"
                         "Bergen"
                         "Hordaland og vestlandet"
                         "Norge"
                         "Internasjonalt (f.eks. USA og England)"]})
              (common/radio-list
               {:name "oppholde"
                :label (str "Hvor ville du sannsynligvis oppholde deg mens du forholder "
                            "deg til nyheter og annen offentlig informasjon?")
                :values ["Hjemme"
                         "Reise (buss, bil, etc)"
                         "Skolen"
                         "Trening, tur"
                         "Konsert eller annet kulturarrangement"
                         "Handletur"]})
              (common/radio-list
               {:name "persinfo"
                :label (str "Er det greit at mediefirma bruker din personlige informasjon "
                            "til journalistiske formål, for eksempel lage en reportasje "
                            "med tekst og bilder?")
                :values ["Veldig greit"
                         "Greit"
                         "Ingen formening"
                         "Ugreit"
                         "Veldig ugreit"]})
              (common/radio-list
               {:name "tilpasset"
                :label "Er det greit at de bruker den til å tilpasse reklame til akkurat deg?"
                :values ["Veldig greit"
                         "Greit"
                         "Ingen formening"
                         "Ugreit"
                         "Veldig ugreit"]})
              (common/radio-list
               {:name "innholdspreferanse"
                :label (str "Hva er det mest sannsynlige scenario når det gjelder "
                            "innholdspreferanser om ti år?")
                :values ["Bruker mer tid på nyheter/seriøst stoff enn nå"
                         "Bruker tiden omtrent som nå"
                         "Bruker mindre tid på nyheter og seriøst stoff"]}))
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
              (common/radio-list
               {:name "imedia"
                :label (str "Hvor ofte har du vært i avisen, på tv, radio, nettavis eller"
                            "andre medier?")
                :values ["To eller flere"
                         "En gang"
                         "Aldri"]})
              (common/radio-list
               {:name "medieaktiv"
                :label "Hvilken medieaktivitet bruker du mest tid på?"
                :values ["Snakke/ringe"
                         "Skrive/lese SMS/Facebook/Twitter, etc."
                         "Ta bilder/redigere"
                         "Ta video/redigere"
                         "Høre musikk"
                         "Programmere"]})
              (common/radio-list
               {:name "mediekanal"
                :label (str "Hvilken mediekanal vil du foretrekke å ytre deg gjennom hvis "
                            "du skulle bidra med et innspill til offentligheten?")
                :values ["Ansikt til ansikt"
                         "Papirbrev"
                         "Telefonsamtale"
                         "SMS"
                         "Epost"
                         "Video"
                         "Kommentar i nettaviser"
                         "Blogg"
                         "Oppføring i et sosialt medium"]})
              (common/radio-list
               {:name "kommuniserer"
                :label "Hvem kommuniserer du mest med på telefonen?"
                :values ["Venner"
                         "Søsken"
                         "Mor"
                         "Far"
                         "Annen familie"
                         "Andre voksne"
                         "Lærere"
                         "Medelever"]})
              (common/radio-table
               {:name "persinfodele"
                :label "Hvilke typer personlig informasjon pleier du å dele på mobilen?"
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
                :values ["Ofte"
                         "Noe"
                         "Sjelden"]})
              (common/radio-table
               {:name "delemedvirksomhet"
                :label (str "Hvis en virksomhet ber deg om å taste inn personlig informasjon " 
                            "på en webside eller applikasjon, hvor villig ville du være til å "
                            "dele med den?")
                :sections ["Idrettslag"
                           "Politiske organisasjoner"
                           "Kommune og stat"
                           "Politiet"
                           "Journalister"
                           "Kommersielle firma som Sas.no og Amazon.com"
                           "Facebook"
                           "Twitter"]
                :values ["Svært"
                         "Noe"
                         "Ikke"]})
              (common/radio-list
               {:name "mobilbruk"
                :label "Hvilke ønsker har du for din egen mobilbruk i fremtiden?"
                :value ["Mer"
                        "Samme som nå"
                        "Mindre"]})
              (common/radio-list
               {:name "mediedeltakelse"
                :label (str "Hva er det mest sannsynlige scenario når det gjelder din "
                            "mediedeltakelse om ti år?")
                :value [(str "Du deltar aktivt i den offentlige sfæren som kjendis og"
                             "viktig person.")
                        "Du kommer med viktige innspill når det trengs, ellers er du stille. "
                        "Du har ingen spesiell rolle i offentligheten"]}))
    :footer (common/footer
             {:content (common/grid-b 
                        {:block-a (common/left-button
                                   {:link "#sjangre-og-innhold"
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
             {:data-position "fixed"
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
  (if (cookies/get :tracker)
    (redirect "/takk")
    (common/layout
     [:form {:action "/takk" :method "post"}
      forside
      deg-selv
      teknologi
      sjangre-og-innhold
      deltagelse
      ferdig])))

(defpage [:post "/takk"] data
  (let [submitter-id (data/generate-submitter-id)]
    (cookies/put! :tracker {:value submitter-id :path "/" :expires 1 :max-age 86400})
    (data/store-answer (assoc data :submitter submitter-id))
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
                        "”Meninger om medier”. Ditt bidrag er verdifullt for forskerne "
                        "ved Institutt for informasjons- og medievitenskap. Vi vil "
                        "formidle resultatene i Bergens Tidende og/eller andre lokale "
                        "medier.")]
               [:p "Følg med!"])
     :footer (common/footer 
              {:data-position "fixed"
               :content [:h1 " "]})})))
