(ns interrogativ.models.parse
  (:require [clojure.string :as str]
            [interrogativ.views.mobile :as mobile]))

(def document #"^(?s).*\z")
(def title #"=+\s*(.*?)\s*=+\n*")
(def page #"(?s)#[^#].*?(?=\n#[^#]|\z)")
(def header #"^#[^#]\s*.*")
(def heading #"(?m)^##+.*")
(def paragraph #"(?s).*?(?=\n\n\n|\n#|\n\s*\?:|\z)")
(def text #"^\s*[^#?+*<-].*")
(def question-block #"(?sm)^\?:.*?(?=\n*?\?:|\n+?#|\n\n|\z)")
(def question-start #"\s*\?:\s*.*")
(def question #"(?s)\s*\?:\s*(.*?)(?=\n\s*[\[&+*<-]|\z)")
(def choice #"(?s)[\[+&*<-]\.?\s*(.*?)(?=\s*\n|\s*\z)")
(def slider #"<(\d+)\s*-\s*(\d+)>\s*:(\d+)")
(def textarea #"\s*\[txt:?(.*)?\]\s*")

(defprotocol Hiccup
  (hiccup [this]))

(defrecord Link [link title content]
  Hiccup
  (hiccup [this]
    [:a {:href link
         :title title}
     content]))

(extend-protocol Hiccup
  nil
  (hiccup [_] nil)
  String
  (hiccup [this]
    this))

(defrecord Heading [size value]
  Hiccup
  (hiccup [this]
    [size value]))

(defrecord Breakline []
  Hiccup
  (hiccup [this]
    [:br]))

(defrecord Paragraph [content]
  Hiccup
  (hiccup [this]
    [:p (map hiccup content)]))

(defrecord TextareaQuestion [name label textarea options]
  Hiccup
  (hiccup [this]
    (mobile/textarea
     {:name name
      :label [:h4 label]
      :value textarea})))

(defrecord SelectQuestion [name label values options]
  Hiccup
  (hiccup [this]
    (mobile/select
     {:name name
      :label [:h4 label]
      :values values})))

(defrecord SliderQuestion [name label min max value options]
  Hiccup
  (hiccup [this]
    (mobile/slider
     {:name name
      :label [:h4 label]
      :max max
      :min min
      :value value})))

(defrecord CheckboxListQuestion [name label values options]
  Hiccup
  (hiccup [this]
    (mobile/checkbox-list
     {:name name
      :label [:h4 label]
      :values values})))

(defrecord CheckboxTableQuestion [name label columns rows values options]
  Hiccup
  (hiccup [this]
    (mobile/checkbox-table
     {:name name
      :label [:h4 label]
      :columns columns
      :rows rows
      :values values})))


(defrecord RadioGroupQuestion [name label groups options]
  Hiccup
  (hiccup [this]
    (mobile/radio-group
     {:name name
      :label [:h4 label]
      :groups groups
      :type (if (some (partial = ":horizontal") options)
              "horizontal")})))

(defrecord RadioTableQuestion [name label columns rows values options]
  Hiccup
  (hiccup [this]
    (mobile/radio-table
     {:name name
      :label [:h4 label]
      :columns columns
      :rows rows
      :values values})))

(defrecord Header [value options])
(defrecord Page [id header content])
(defrecord Document [title survey thankyou])

(defn remove-line [string]
  (str/replace-first string #".*(\n|\z)" ""))

(defn first-line [string]
  (re-find #".*" string))

(defn parse-header [header]
  (->Header
   (->> header
        (re-find #"#\s*(.*?)(?=\s*:\w+|\s*$)")
        second
        str/trim)
   (re-seq #":\w+" header)))

(defn parse-heading [heading]
  (->Heading
   (->> heading
        str/trimr
        (re-find #"#+")
        count
        (format "h%s")
        keyword)
   (second (re-find #"#+\s*(.*)" heading))))

(defn parse-question [nb question-block]
  (let [name (format "Q%02d" nb)
        question (second (re-find question question-block))
        label (str nb ". " (-> question
                               (str/replace #"\n+|:\w+\s*" " ")
                               (str/replace #"\s+" " ")
                               str/trim))
        options (re-seq #":\w+" question)
        question-block (str/replace-first question-block question "")
        choices (map first (re-seq choice question-block))]
    (cond (empty? choices)
          (->SelectQuestion
           name
           label
           ["missing values"]
           options)
          
          (not-empty (filter (partial re-matches #"^\*.*") choices))
          (->RadioTableQuestion
           name
           label
           (map (comp second (partial re-find choice))
                (filter (partial re-matches #"^\+.*") choices))
           (map (comp second (partial re-find choice))
                (filter (partial re-matches #"^-.*") choices))
           (map (comp second (partial re-find choice))
                (filter (partial re-matches #"^\*.*") choices))
           options)

          (and (not-empty (filter (partial re-matches #"^-.*") choices))
               (not-empty (filter (partial re-matches #"^&.*") choices)))
          (->CheckboxTableQuestion
           name
           label
           (map (comp second (partial re-find choice))
                (filter (partial re-matches #"^\+.*") choices))
           (map (comp second (partial re-find choice))
                (filter (partial re-matches #"^-.*") choices))
           (map (comp second (partial re-find choice))
                (filter (partial re-matches #"^&.*") choices))
           options)

          (re-matches slider (first choices))
          (let [slider (re-find slider (first choices))]
            (->SliderQuestion
             name
             label
             (nth slider 1)
             (nth slider 2)
             (nth slider 3)
             options))

          (re-matches textarea (first choices))
          (let [textarea (second (re-find textarea (first choices)))]
            (->TextareaQuestion
             name
             label
             textarea
             options))

          (empty? (remove (partial re-matches #"^\+.*") choices))
          (->SelectQuestion
           name
           label
           (map (comp second (partial re-find choice))
                choices)
           options)

          (not-empty (filter (partial re-matches #"^-.*") choices))
          (->RadioGroupQuestion
           name
           label
           (map (comp second (partial re-find choice))
                choices)
           options)

          (not-empty (filter (partial re-matches #"^&.*") choices))
          (->CheckboxListQuestion
           name
           label
           (map (comp second (partial re-find choice))
                choices)
           options))))

(defn parse-links [s]
  (loop [s s
         vals []]
    (if-let [link (re-find #"\[.+?\]\(\s*\S+\s*?\"?\w*?\"?\s*\)" s)]
      (let [content (re-find #"(?<=\[).+(?=\])" link)
            href (re-find #"(?<=\()\S+(?=\s|\"|\))" link)
            title (re-find #"(?<=\").+(?=\")" link)
            [pre post] (str/split (str/replace-first s link "===LINK===") #"===LINK===" 2)] 
        (recur post
               (conj vals
                     (if-not (str/blank? pre) pre)
                     (->Link href title content))))
      (remove nil? (seq (conj vals (if-not (str/blank? s) s)))))))

(defn parse-paragraph [paragraph]
  (->Paragraph
   (apply concat
          (interpose (list (->Breakline))
                     (map #(parse-links (str/replace % #"\n" " "))
                          (str/split paragraph #"\n\n"))))))

(defn parse-page [page-id question-id page-text]
  (loop [content (remove-line page-text)
         question-id question-id
         page []]
    (if (str/blank? content)
      [(->Page
        (str "page-" page-id)
        (parse-header (first-line page-text))
        page)
       question-id]
      (let [line (first-line content)]
        (cond (re-matches heading line)
              (recur (remove-line content)
                     question-id
                     (conj page (parse-heading line)))
              
              (re-matches question-start line)
              (let [question-block (re-find question-block content)]
                (recur (str/replace-first content question-block "")
                       (inc question-id)
                       (conj page (parse-question question-id
                                                  question-block))))

              (re-matches text line)
              (let [paragraph (re-find paragraph content)]
                (recur (str/replace-first content paragraph "")
                       question-id
                       (conj page (parse-paragraph paragraph))))

              :else
              (recur (remove-line content) question-id page))))))

(defn submit-page? [page]
  (some (partial = ":submit")
        (get-in page [:header :options])))

(defn parse-document [document]
  (loop [document document
         page-id 1
         question-id 1
         submit? false
         [survey post] [[] []]]
    (let [line (first-line document)
          page-text (re-find page document)]
      (if (str/blank? document)
        [survey post]
        (if page-text
          (let [[page question-id]  (parse-page page-id
                                                question-id
                                                page-text)]
            (recur (str/replace-first document page-text "")
                   (if (submit-page? page) 1 (inc page-id))
                   question-id
                   (or submit? (submit-page? page))
                   (if submit?
                     [survey (conj post page)]
                     [(conj survey page) post])))
          (recur (remove-line document)
                 page-id
                 question-id
                 submit?
                 [survey post]))))))

(defn parse [document]
  (let [title (re-find title document)
        [survey thankyou]
        (parse-document
         (str/replace-first
          document
          (if (nil? title) "" (first title))
          ""))]
    (->Document
     (second title)
     survey
     thankyou)))
