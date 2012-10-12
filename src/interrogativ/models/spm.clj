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
(def question #"\s*\?:\s*(.*)")
(def choice #"(?s)[+*<-]\.?.*?(?=\n|\z)")
(def slider #"<(\d+)\s*-\s*(\d+)>\s*:(\d+)")

(def page-nb (atom 0))
(def question-nb (atom 0))

(defn remove-line [string]
  (string/replace-first string #".*\n" ""))

(defn first-line [string]
  (re-find #".*" string))

(defn parse-header [header]
  (common/header
   {:content (html (string/trim (second (re-find #"#\s*(.*?)(?=\s*:\w+|\s*$)" header)))
                   [:a {:class "ui-btn-right"} "1 / %PAGE_NB%"])}))

(defn parse-footer [page-id]
  (common/footer
   {:id (format "footer-p%s" page-id)
    :content (common/grid-b
              {:block-a (if (< 1 page-id)
                          (common/right-button
                           {:link (format "#page-%s" (dec page-id))
                            :label "Tilbake"
                            :inline "false"}))
               :block-c (common/right-button
                         {:link (format "#page-%s" (inc page-id))
                          :label "Neste"
                          :inline "false"})})}))

(defn parse-heading [heading]
  [(keyword (format "h%s" (count (re-find #"#+" (string/trimr heading)))))
   (second (re-find #"#+\s*(.*)" heading))])

(defn parse-question [question-block]
  (let [nb (swap! question-nb inc)
        name (format "spm-%s" nb)
        label (format "%s. %s"
                      nb
                      (string/replace (second (re-find question question-block))
                                      #"\n+|:\w+" " "))
        choices (re-seq choice question-block)]
    (cond (not-empty (filter (partial re-matches #"^\*.*") choices))
          (common/radio-table
           {:name name
            :label label
            :sections (filter (partial re-matches #"^-.*") choices)
            :values (filter (partial re-matches #"^\*.*") choices)})
          
          (re-matches slider (first choices))
          (let [slider (re-find slider (first choices))]
            (common/slider
             {:name name
              :label label
              :min (nth slider 1)
              :max (nth slider 2)
              :value (nth slider 3)}))
          
          (empty? (remove (partial re-matches #"^\+.*") choices))
          (common/select
           {:name name
            :label label
            :values choices})
          
          (not-empty (filter (partial re-matches #"^-.*") choices))
          (common/radio-group
           {:name name
            :label [:h4 label]
            :groups choices}))))

(defn parse-paragraph [paragraph]
  (interpose [:br]
             (map #(string/replace % #"\n" " ")
                  (string/split paragraph #"\n\n"))))

(defn parse-document [document]
  (let [line (first-line document)]
    (cond (string/blank? document)
          document

          (re-matches header line)
          (let [page (re-find page document)
                page-id (swap! page-nb inc)]
            (html (common/page
                   {:id (format "page-%s" page-id)
                    :header (parse-header line)
                    :content (common/content
                              (parse-document (remove-line page)))
                    :footer (parse-footer page-id)})
                  (parse-document (string/replace-first document page ""))))
          
          (re-matches heading line)
          (html (parse-heading line)
                (parse-document (remove-line document)))

          (re-matches question line)
          (let [question-block (re-find question-block document)]
            (html (parse-question question-block)
                  (parse-document (string/replace-first document question-block ""))))

          (re-matches text line)
          (let [paragraph (re-find paragraph document)]
            (html [:p  (parse-paragraph paragraph)]
                  (parse-document (string/replace-first document paragraph ""))))

          :else
          (parse-document (remove-line document)))))

(defn parse [spm]
  (let [document (slurp spm)
        title (re-find title document)]
    (common/layout
     {:title (second title)
      :body (common/body
             (string/replace
              (parse-document
               (string/replace-first document
                                     (first title) ""))
              "%PAGE_NB%"
              (str @page-nb)))})))