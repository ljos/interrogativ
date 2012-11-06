(ns interrogativ.models.spm
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.zip :as zip]
            [interrogativ.views.common :as common])
  (:use [hiccup.core :only [html]])
  (:import [java.io BufferedReader StringReader]
           [java.util.regex Pattern]))

(def document #"^(?s).*\z")
(def title #"=+\s*(.*?)\s*=+\n*")
(def page #"(?s)#[^#].*?(?=\n#[^#]|\z)")
(def header #"^#[^#]\s*.*")
(def heading #"(?m)^##+.*")
(def paragraph #"(?s).*?(?=\n\n\n|\n#|\n\s*\?:|\z)")
(def text #"^\s*[^#?+*<-].*")
(def question-block #"(?sm)^\?:.*?(?=\n*?\?:|\n+?#|\n\n|\z)")
(def question-start #"\s*\?:\s*.*")
(def question #"(?s)\s*\?:\s*(.*?)(?=\n\s*[\[+*<-]|\z)")
(def choice #"(?s)[\[+*<-]\.?\s*(.*?)(?=\s*\n|\s*\z)")
(def slider #"<(\d+)\s*-\s*(\d+)>\s*:(\d+)")
(def textarea #"\s*\[txt:?(.*)?\]\s*")

(def page-nb (atom 0))
(def question-nb (atom 0))
(def submit-page (atom :not-set))

(defn remove-line [string]
  (str/replace-first string #".*(\n|\z)" ""))

(defn first-line [string]
  (re-find #".*" string))

(defn parse-header [header]
  {:type :header
   :value (->> header
               (re-find #"#\s*(.*?)(?=\s*:\w+|\s*$)")
               second
               str/trim)
   :options (re-seq #":\w+" header)})

(defn parse-heading [heading]
  {:type :heading
   :h (->> heading
           str/trimr
           (re-find #"#+")
           count
           (format "h%s")
           keyword)
   :value (second (re-find #"#+\s*(.*)" heading))})

(defn parse-question [question-block]
  (let [nb (swap! question-nb inc)
        name (format "spm-%s" nb)
        question (second (re-find question question-block))
        label (str nb ". " (-> question
                               (str/replace #"\n+|:\w+\s*" " ")
                               str/trim))
        options (second (re-seq #":\w+" question))
        choices (map first (re-seq choice question-block))]
    (cond (empty? choices)
          {:type :question
           :question :select
           :label label
           :options options
           :values ["missing values"]}
          
          (not-empty (filter (partial re-matches #"^\*.*") choices))
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

          (re-matches textarea (first choices))
          (let [textarea (second (re-find textarea (first choices)))]
            {:type :question
             :question :textarea
             :name name
             :label label
             :options options
             :value textarea})

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
           :label label
           :options options
           :groups (map (comp second (partial re-find choice))
                        choices)})))

(defn parse-paragraph [paragraph]
  (interpose {:type :br}
             (map #(str/replace % #"\n" " ")
                  (str/split paragraph #"\n\n"))))

(defn parse-document [document]
  (let [line (first-line document)]
    (cond (str/blank? document)
          (str/trim document)

          (re-matches header line)
          (let [page (re-find page document)
                page-id (swap! page-nb inc)]
            (cons {:type :page
                   :id (format "page-%s" page-id)
                   :header (parse-header line)
                   :content (parse-document (remove-line page))}
                  (parse-document
                   (str/replace-first document page ""))))

          (re-matches heading line)
          (cons (parse-heading line)
                (parse-document (remove-line document)))

          (re-matches question-start line)
          (let [question-block (re-find question-block document)]
            (cons (parse-question question-block)
                  (parse-document
                   (str/replace-first document question-block ""))))

          ;;; should create a match for unordered lists
          ;;; maybe even ordered lists as well
          
          (re-matches text line)
          (let [paragraph (re-find paragraph document)]
            (cons {:type :p :content (parse-paragraph paragraph)}
                  (parse-document
                   (str/replace-first document paragraph ""))))

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
            (str/replace-first
             document (if (nil? title)
                        "" (first title)) ""))}))