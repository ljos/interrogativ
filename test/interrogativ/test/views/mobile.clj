(ns interrogativ.test.views.mobile
  (:use interrogativ.views.mobile
        midje.sweet))

(fact "Check hiccup of left-button."
  (left-button {:link "#page-1"})
  => [:a {:href "#page-1"
          :data-role "button"
          :id nil
          :data-icon "arrow-l"
          :data-inline "false"}
      "Tilbake"])

(fact "Check hiccup of right-button"
  (right-button {:link "#page-2"})
  =>  [:a {:href "#page-2"
           :data-role "button"
           :id nil
           :data-icon "arrow-r"
           :data-iconpos "right"
           :data-inline "false"}
       "Neste"])

(fact "Check hiccup of submit-button"
  (submit-button)
  => [:input {:data-icon "arrow-r"
              :data-iconpos "right"
              :data-inline "false"
              :type "submit"
              :name "submitter"
              :value "Levér"}])

(fact "check hiccup of menu-button"
  (menu-button {:label "Menu"})
  => [:a {:class "ui-btn-right"
       :href "#meny"
       :data-icon "gear"
       :data-iconpos "right"
       :data-rel "dialog"
       :data-transition "slidedown"}
      "Menu"])

(fact "Check hiccup of header"
  (header {:content [:h1 "Header"]})
  => [:div {:data-role "header"
            :data-position nil
            :data-theme "a"}
      [:h1 "Header"]])

(fact "Check hiccup of footer"
  (footer {:content [:h1 "Footer"]})
  => [:div {:id nil
            :data-role "footer"
            :data-position nil}
      [:h1 "Footer"]])

(fact "Check hiccup of page"
  (page {:content [:p "Paragraph paragraph"]})
  => [:div {:data-role "page"
            :id nil
            :data-title nil
            :data-theme nil}
      nil
      [:p "Paragraph paragraph"]
      nil])

(fact "Check hiccup of content"
  (content [:p "first p"] [:p "second p"])
  => [:div {:data-role "content"}
      (list [:p "first p"]
            [:p "second p"])
      [:br]
      [:br]])

(fact "Check hiccup of radio-list"
  (radio-list {:name "Q01" :label "1. Question?" :values '("a" "b" "c")})
  => [:fieldset {:data-role "controlgroup" :data-type nil}
      (list [:legend "1. Question?"]
            [:input {:type "radio"
                     :name "Q01"
                     :id "Q01C01"
                     :value 1}]
            [:label {:for "Q01C01"} "a"]
            [:input {:type "radio"
                     :name "Q01"
                     :id "Q01C02"
                     :value 2}]
            [:label {:for "Q01C02"} "b"]
            [:input {:type "radio"
                     :name "Q01"
                     :id "Q01C03"
                     :value 3}]
            [:label {:for "Q01C03"} "c"])])

(fact "Check hiccup of slider"
  (slider {:name "Q02" :label "2. Question?" :value 2 :min 1 :max 3})
  => (list [:label {:for "Q02"} "2. Question?"]
           [:input {:type "range"
                    :name "Q02"
                    :id "Q02"
                    :value 2
                    :min 1
                    :max 3}]))

(fact "Check hiccup of select"
  (select {:name "Q03" :label "3. Question?" :values '("a" "b" "c")})
  => (list [:label {:for "Q03" :class "select"} "3. Question?"]
        [:select {:name "Q03" :id "Q03"}
         (list [:option {:value 1} "a"]
               [:option {:value 2} "b"]
               [:option {:value 3} "c"])]))

(fact "Check hiccup of textarea"
  (textarea {:name "Q04" :label "4. Question?" :value "??"})
  => (list [:label {:for "Q04"} "4. Question?"]
           [:textarea {:name "Q04" :id "Q04"} "??"]))

(fact "Check hiccup of checkbox-list"
  (checkbox-list {:name "Q06" :label "6. Question?" :values '("a" "b" "c")})
  => [:fieldset {:data-role "controlgroup" :data-type nil}
      (list [:legend "6. Question?"]
            [:input {:type "checkbox"
                     :name "Q06C01"
                     :id "Q06C01"
                     :value 1}]
            [:label {:for "Q06C01"} "a"]
            [:input {:type "checkbox"
                     :name "Q06C02"
                     :id "Q06C02"
                     :value 1}]
            [:label {:for "Q06C02"} "b"]
            [:input {:type "checkbox"
                     :name "Q06C03"
                     :id "Q06C03"
                     :value 1}]
            [:label {:for "Q06C03"} "c"])])
