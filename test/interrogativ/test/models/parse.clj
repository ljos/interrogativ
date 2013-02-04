(ns interrogativ.test.models.parse
  (:use interrogativ.models.parse)
  (:use midje.sweet))

(fact (remove-line "First line\nSecond line") => "Second line")
(fact (first-line "First line\nSecond line") => "First line")

(fact (parse-header "# A Header :post") => (->Header "A Header" '(":post")))

(fact (parse-heading "## A heading") => (->Heading :h2 "A heading"))

(fact (parse-question 1 "?: Er du gutt eller jente? :horizontal
   - Gutt
   - Jente") => (->RadioGroupQuestion
                 "Q01"
                 "1. Er du gutt eller jente?"
                 '("Gutt" "Jente")
                 '(":horizontal")))

(fact (parse-question 2 "?: Hvor gammel er du?
   <17 - 35> :20") => (->SliderQuestion
                       "Q02"
                       "2. Hvor gammel er du?"
                       "17"
                       "35"
                       "20"
                       nil))

(fact (parse-question 3 "?: Liker du å forholde deg til flere skjermer samtidig,
   for eksempel at du både ser på TV og leser på mobil?
   - Liker det
   - Nøytral
   - Liker det ikke") => (->RadioGroupQuestion
                          "Q03"
                          (str "3. Liker du å forholde deg til flere skjermer samtidig, "
                               "for eksempel at du både ser på TV og leser på mobil?")
                          '("Liker det" "Nøytral" "Liker det ikke")
                          nil))

(fact (parse-question 4 "?: Hva slags mobiltelefon har du?
   + iPhone
   + Samsung") => (->SelectQuestion
                   "Q04"
                   "4. Hva slags mobiltelefon har du?"
                   '("iPhone" "Samsung")
                   nil))

(fact (parse-question 5 "?: Hvordan vil du helst skrive?
   - Tastatur
   - Touchskjerm
   - Håndskrift
   * +
   * •
   * −") => (->RadioTableQuestion
             "Q05"
             "5. Hvordan vil du helst skrive?"
             '()
             '("Tastatur"
               "Touchskjerm"
               "Håndskrift")
             '("+" "•" "−")
             nil))

(fact (parse-question 6 "?: Har du noe du vil si? Vi tar det på største alvor i
   våre analyser.
  [txt:Skriv her]") => (->TextareaQuestion
                         "Q06"
                         "6. Har du noe du vil si? Vi tar det på største alvor i våre analyser."
                         "Skriv her"
                         nil))

(fact (parse-question 7 "?: Hvilke tekniske ting har du?
 & Tablet/iPad 
 & Smarttelefon
 & Mobil uten mulighet til nett
 & Mobil med mulighet til nett
 ") => (->CheckboxListQuestion
        "Q07"
        "7. Hvilke tekniske ting har du?"
        '("Tablet/iPad"
          "Smarttelefon"
          "Mobil uten mulighet til nett"
          "Mobil med mulighet til nett")
        nil))

(fact (parse-question 8 "?: Hva av dette kunne du tenkt deg å gjøre med venner?
 - Shopping 
 - Pub 
 & ?
 & ?") => (->CheckboxTableQuestion
           "Q08"
           "8. Hva av dette kunne du tenkt deg å gjøre med venner?"
           '()
           '("Shopping" "Pub")
           '("?" "?")
           nil))

(fact (parse-question 9 "?: Hva av dette kunne du tenkt deg å gjøre med venner?
 + Venner
 + Andre
 - Shopping
 - Pub 
 & ?
 & ?") => (->CheckboxTableQuestion
           "Q09"
           "9. Hva av dette kunne du tenkt deg å gjøre med venner?"
           '("Venner" "Andre")
           '("Shopping" "Pub")
           '("?" "?")
           nil))

(fact (parse-question 10 "?: Hvordan vil du helst skrive?
   - Tastatur
   - Touchskjerm
   - Håndskrift
   + Mye
   + Passe
   + Lite
   * +
   * •
   * −") => (->RadioTableQuestion
             "Q10"
             "10. Hvordan vil du helst skrive?"
             '("Mye" "Passe" "Lite")
             '("Tastatur" "Touchskjerm" "Håndskrift")
             '("+" "•" "−")
             nil))

(fact (parse-paragraph "Institutt for informasjons- og medievitenskap

Universitetet i Bergen") => (->Paragraph
                             (list  "Institutt for informasjons- og medievitenskap"
                                    (->Breakline)
                                    "Universitetet i Bergen")))

(fact (parse-paragraph "line1
line2
lin3") => (->Paragraph
           '("line1 line2 lin3")))

(fact (parse-page 1 1 "# Spørreundersøkelse
## Hvilke nye medier trenger Norge mest?
Hvis du skulle tenke deg et helt nytt og annerledes medium; hvordan
skulle det være?") => [(->Page
                        "page-1"
                        (parse-header "# Spørreundersøkelse")
                        [(parse-heading "## Hvilke nye medier trenger Norge mest?")
                         (parse-paragraph "Hvis du skulle tenke deg et helt nytt og annerledes medium; hvordan
skulle det være?")]) 1])

(fact (submit-page? (->Page
                     "page-1"
                     (->Header
                      "Header"
                      '(":submit"))
                     nil)) => true)

(fact "Should not interpret `-` inside a question as a choice"
  (parse-question 11 "?: Søke i NRK-arkivet dersom deler var tilgjengelig på nettsiden?
   - Ja
   - Nei") => (->RadioGroupQuestion
               "Q11"
               "11. Søke i NRK-arkivet dersom deler var tilgjengelig på nettsiden?"
               '("Ja" "Nei")
               nil))
