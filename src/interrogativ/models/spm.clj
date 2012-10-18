(ns interrogativ.models.spm
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.zip :as zip]
            [interrogativ.views.common :as common])
  (:use [hiccup.core :only [html]])
  (:import [java.io BufferedReader StringReader]
           [java.util.regex Pattern]))

(def document #"^(?s).*\z")
(def title #"===\s*(.*?)\s*===\n*")
(def page #"(?s)#[^#].*?(?=\n#[^#]|\z)")
(def header #"^#[^#]\s*.*")
(def heading #"(?m)^##+.*")
(def paragraph #"(?s).*?(?=\n\n\n|\n#|\z)")
(def text #"^\s*[^#?+*<-].*")
(def question-block #"(?sm)^\?:.*?(?=\n*?\?:|\n+?#|\n*?\z)")
(def question-start #"\s*\?:\s*.*")
(def question #"(?s)\s*\?:\s*(.*?)(?=\n\s*[+*<-])")
(def choice #"(?s)[+*<-]\.?\s*(.*?)(?=\s*\n|\s*\z)")
(def slider #"<(\d+)\s*-\s*(\d+)>\s*:(\d+)")

(def page-nb (atom 0))
(def question-nb (atom 0))
(def submit-page (atom :not-set))

(defn remove-line [string]
  (string/replace-first string #".*\n" ""))

(defn first-line [string]
  (re-find #".*" string))

(defn parse-header [header]
  {:type :header
   :value (string/trim
           (second
            (re-find #"#\s*(.*?)(?=\s*:\w+|\s*$)"
                     header)))
   :options (re-seq #":\w+" header)})

(defn parse-heading [heading]
  {:type :heading
   :h (keyword
       (format "h%s"
               (count (re-find #"#+"
                               (string/trimr heading)))))
   :value (second (re-find #"#+\s*(.*)" heading))})

(defn parse-question [question-block]
  (let [nb (swap! question-nb inc)
        name (format "spm-%s" nb)
        question (second (re-find question question-block))
        label (format "%s. %s"
                      nb
                      (string/trim
                       (string/replace
                        question
                        #"\n+|:\w+\s*" " ")))
        options (second (re-seq #":\w+" question))
        choices (map first (re-seq choice question-block))]
    (cond (not-empty (filter (partial re-matches #"^\*.*") choices))
          {:type :question
           :question :radio-table
           :name name
           :label label
           :options options
           :sections (map (comp second (partial re-find choice))
                          (filter (partial re-matches #"^-.*") choices))
           :values (map (comp second (partial re-find choice))
                        (filter (partial re-matches #"^\*.*") choices))}

          (re-matches slider (first choices))
          (let [slider (re-find slider (first choices))]
            {:type :question
             :question :slider
             :name name
             :label label
             :options options
             :min (nth slider 1)
             :max (nth slider 2)
             :value (nth slider 3)})

          (empty? (remove (partial re-matches #"^\+.*") choices))
          {:type :question
           :question :select
           :name name
           :label label
           :options options
           :values (map (comp second (partial re-find choice))
                        choices)}

          (not-empty (filter (partial re-matches #"^-.*") choices))
          {:type :question
           :question :radio-group
           :name name
           :label [:h4 label]
           :options options
           :groups (map (comp second (partial re-find choice))
                        choices)})))

(defn parse-paragraph [paragraph]
  (interpose {:type :br}
             (map #(string/replace % #"\n" " ")
                  (string/split paragraph #"\n\n"))))

(defn parse-document [document]
  (let [line (first-line document)]
    (cond (string/blank? document)
          (string/trim document)

          (re-matches header line)
          (let [page (re-find page document)
                page-id (swap! page-nb inc)]
            (cons {:type :page
                   :id (format "page-%s" page-id)
                   :header (parse-header line)
                   :content (parse-document (remove-line page))}
                  (parse-document
                   (string/replace-first document page ""))))

          (re-matches heading line)
          (cons (parse-heading line)
                (parse-document (remove-line document)))

          (re-matches question-start line)
          (let [question-block (re-find question-block document)]
            (cons (parse-question question-block)
                  (parse-document
                   (string/replace-first document question-block ""))))

          (re-matches text line)
          (let [paragraph (re-find paragraph document)]
            (cons {:type :p :content (parse-paragraph paragraph)}
                  (parse-document
                   (string/replace-first document paragraph ""))))

          :else
          (parse-document (remove-line document)))))

(defn parse [spm]
  (reset! page-nb 0)
  (reset! question-nb 0)
  (let [document (slurp spm)
        title (re-find title document)]
    {:type :document
     :title (second title)
     :body (parse-document
            (string/replace-first
             document (first title) ""))}))