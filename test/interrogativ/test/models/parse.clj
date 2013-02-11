(ns interrogativ.test.models.parse
  (:use interrogativ.models.parse)
  (:use midje.sweet))

(fact "Remove first line of a string"
  (remove-line "First line\nSecond line")
  => "Second line")

(fact "Get first line of a string."
  (first-line "First line\nSecond line")
  => "First line")

(fact "Parse header."
  (parse-header "# A Header :post")
  => (->Header "A Header" '(":post")))

(fact "Parse heading."
  (parse-heading "## A heading")
  => (->Heading :h2 "A heading"))

(def q01-radiogroup (->RadioGroupQuestion
                     "Q01"
                     "1. Er du gutt eller jente?"
                     '("Gutt" "Jente")
                     '(":horizontal")))

(fact "Parse horizontal radiogroup."
  (parse-question 1 "?: Er du gutt eller jente? :horizontal
   - Gutt
   - Jente")
  => q01-radiogroup)

(fact "Overview of radio group"
  (overview q01-radiogroup)
  => "Radio group:
Q01 : 1. Er du gutt eller jente?
  value: 1 label: Gutt
  value: 2 label: Jente

")

(def q02-slider (->SliderQuestion
                 "Q02"
                 "2. Hvor gammel er du?"
                 "17"
                 "35"
                 "20"
                 nil))

(fact "Parse slider."
  (parse-question 2 "?: Hvor gammel er du?
   <17 - 35> :20")
  => q02-slider)

(fact "Overview of slider."
  (overview q02-slider)
  => "Slider:
Q02 : 2. Hvor gammel er du?
  range: 17 - 35

")

(fact "Parse radiogroup."
  (parse-question 3 "?: Liker du å forholde deg til flere skjermer samtidig,
   for eksempel at du både ser på TV og leser på mobil?
   - Liker det
   - Nøytral
   - Liker det ikke")
  => (->RadioGroupQuestion
      "Q03"
      (str "3. Liker du å forholde deg til flere skjermer samtidig, "
           "for eksempel at du både ser på TV og leser på mobil?")
      '("Liker det" "Nøytral" "Liker det ikke")
      nil))

(def q04-select (->SelectQuestion
                 "Q04"
                 "4. Hva slags mobiltelefon har du?"
                 '("iPhone" "Samsung")
                 nil))

(fact "Parse select."
  (parse-question 4 "?: Hva slags mobiltelefon har du?
   + iPhone
   + Samsung")
  => q04-select)

(fact "Overview of select"
  (overview q04-select)
  => "Select:
Q04 : 4. Hva slags mobiltelefon har du?
  value: 1 choice: iPhone
  value: 2 choice: Samsung

")

(def q05-radiotable (->RadioTableQuestion
                     "Q05"
                     "5. Hvordan vil du helst skrive?"
                     '()
                     '("Tastatur"
                       "Touchskjerm"
                       "Håndskrift")
                     '("+" "•" "−")
                     nil))

(fact "Parse radio table."
  (parse-question 5 "?: Hvordan vil du helst skrive?
   - Tastatur
   - Touchskjerm
   - Håndskrift
   * +
   * •
   * −")
  => q05-radiotable)

(fact "Overview radio table."
  (overview q05-radiotable)
  => "Radio table:
Q05 : 5. Hvordan vil du helst skrive?
  Q05R01 : Tastatur
    value: 1 label: +
    value: 2 label: •
    value: 3 label: −
  Q05R02 : Touchskjerm
    value: 1 label: +
    value: 2 label: •
    value: 3 label: −
  Q05R03 : Håndskrift
    value: 1 label: +
    value: 2 label: •
    value: 3 label: −

")

(def q06-textarea (->TextareaQuestion
                   "Q06"
                   "6. Har du noe du vil si? Vi tar det på største alvor i våre analyser."
                   "Skriv her"
                   nil))

(fact "Parse text question."
  (parse-question 6 "?: Har du noe du vil si? Vi tar det på største alvor i
   våre analyser.
  [txt:Skriv her]") => q06-textarea)

(fact "Textarea question should produce overview."
  (overview q06-textarea)
  => (str "Textarea:
Q06 : 6. Har du noe du vil si? Vi tar det på største alvor i våre analyser.

"))

(def q07-checkboxlist (->CheckboxListQuestion
                       "Q07"
                       "7. Hvilke tekniske ting har du?"
                       '("Tablet/iPad"
                         "Smarttelefon"
                         "Mobil uten mulighet til nett"
                         "Mobil med mulighet til nett")
                       nil))

(fact "Parse checkboxlist."
  (parse-question 7 "?: Hvilke tekniske ting har du?
 & Tablet/iPad
 & Smarttelefon
 & Mobil uten mulighet til nett
 & Mobil med mulighet til nett
 ")
  => q07-checkboxlist)

(fact "Overview checkbox list."
  (overview q07-checkboxlist)
  => "Checkbox list:
Q07 : 7. Hvilke tekniske ting har du?
  Q07C01 : Tablet/iPad
  Q07C02 : Smarttelefon
  Q07C03 : Mobil uten mulighet til nett
  Q07C04 : Mobil med mulighet til nett

")


(def q08-checkboxtable (->CheckboxTableQuestion
                        "Q08"
                        "8. Hva av dette kunne du tenkt deg å gjøre med venner?"
                        '()
                        '("Shopping" "Pub")
                        '("?" "?")
                        nil))

(fact "parse checkbox table without header."
  (parse-question 8 "?: Hva av dette kunne du tenkt deg å gjøre med venner?
 - Shopping
 - Pub
 & ?
 & ?")
  => q08-checkboxtable)

(fact "Overview checkbox table"
  (overview q08-checkboxtable)
  => "Checkbox table:
Q08 : 8. Hva av dette kunne du tenkt deg å gjøre med venner?
  Q08R01C01 : row Shopping value ?
  Q08R01C02 : row Shopping value ?
  Q08R02C01 : row Pub value ?
  Q08R02C02 : row Pub value ?

")

(fact "Parse checkboxtable with header"
  (parse-question 9 "?: Hva av dette kunne du tenkt deg å gjøre med venner?
 + Venner
 + Andre
 - Shopping
 - Pub
 & ?
 & ?")
  => (->CheckboxTableQuestion
      "Q09"
      "9. Hva av dette kunne du tenkt deg å gjøre med venner?"
      '("Venner" "Andre")
      '("Shopping" "Pub")
      '("?" "?")
      nil))

(fact "Parse radio table with header."
  (parse-question 10 "?: Hvordan vil du helst skrive?
   - Tastatur
   - Touchskjerm
   - Håndskrift
   + Mye
   + Passe
   + Lite
   * +
   * •
   * −")
  => (->RadioTableQuestion
      "Q10"
      "10. Hvordan vil du helst skrive?"
      '("Mye" "Passe" "Lite")
      '("Tastatur" "Touchskjerm" "Håndskrift")
      '("+" "•" "−")
      nil))

(fact "Parse parahraph with breakline."
  (parse-paragraph "Institutt for informasjons- og medievitenskap

Universitetet i Bergen") => (->Paragraph
                             (list  "Institutt for informasjons- og medievitenskap"
                                    (->Breakline)
                                    "Universitetet i Bergen")))

(fact "Parse paragraph without breakline."
  (parse-paragraph "line1
line2
lin3")
  => (->Paragraph
      '("line1 line2 lin3")))

(fact "Parse page."
  (parse-page "page-1" 1 "# Spørreundersøkelse
## Hvilke nye medier trenger Norge mest?
Hvis du skulle tenke deg et helt nytt og annerledes medium; hvordan
skulle det være?")
  => [(->Page
       "page-1"
       (parse-header "# Spørreundersøkelse")
       [(parse-heading "## Hvilke nye medier trenger Norge mest?")
        (parse-paragraph "Hvis du skulle tenke deg et helt nytt og annerledes medium; hvordan
skulle det være?")]) 1])

(fact "Check if is submit-page."
  (submit-page? (->Page
                 "page-1"
                 (->Header
                  "Header"
                  '(":submit"))
                 nil))
  => truthy)

(fact "Ensure that submit-page does not return true for everything"
  (submit-page? (->Page
                 "page-2"
                 (->Header
                  "Header"
                  nil)
                 nil))
  =not=> truthy)

(fact "Should not interpret `-` inside a question as a choice"
  (parse-question 11 "?: Søke i NRK-arkivet dersom deler var tilgjengelig på nettsiden?
   - Ja
   - Nei")
  => (->RadioGroupQuestion
      "Q11"
      "11. Søke i NRK-arkivet dersom deler var tilgjengelig på nettsiden?"
      '("Ja" "Nei")
      nil))

(fact "Should parse links in paragraphs."
  (parse-paragraph "Lenke til [http://bt.no](http://bt.no).")
  => (->Paragraph (list "Lenke til " (->Link "http://bt.no" nil "http://bt.no") ".")))
