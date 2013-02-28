(ns interrogativ.parser.parse
  (:require [clojure.string :as str]
            [interrogativ.parser.types :refer :all]
            [interrogativ.parser.tokenize :as token])
  (:refer-clojure :exclude [newline])
  (:import [interrogativ.parser.types
            Document
            Title
            Page
            Header
            RadioTableQuestion
            RadioGroupQuestion
            CheckboxTableQuestion
            CheckboxListQuestion
            SliderQuestion
            SelectQuestion
            TextareaQuestion
            Paragraph
            Breakline
            Heading
            Link]))

(defmacro or->
  "Short-circuits when one of the functions returns non-nil.
This includes false and '() as it is not= nil."
  ([arg form]
     `(-> ~arg ~form))
  ([arg form & more]
     `(let [res# (or-> ~arg ~form)]
        (if (nil? res#)
          (or-> ~arg ~@more)
          res#))))

(defmacro defparser
  "Creates a parser given a regex and name + a body."
  [name regex & more]
  `(defn ~name [[document# ~'parse-tree]]
     (if-let [~'token  (re-find ~(re-pattern (str "^" regex))
                                document#)]
       (let [document# (str/replace-first document#
                                          (if (string? ~'token)
                                            ~'token (first ~'token))
                                          "")]
         (if-let [parse-tree# ((fn [] ~@more))]
           [document#
            (conj ~'parse-tree
                  parse-tree#)])))))

(defparser header #"^#[^#]\s*(.*)"
  (Header. (second token) nil))

(defparser heading #"(?m)^#(#+)(.*)"
  (Heading. (count (second token))
            (last token)))

(defparser question #"(?s)\s*\?:\s*(.*?)(?=\n\s*[\[&+*<-]|\z)"
  (second question))

(defparser choice+ #"\s*\+(.*)"
  (->Choice+ (second token)))

(defparser choice& #"\s*&(.*)"
  (->Choice& (second token)))

(defparser choice* #"\s*\*(.*)"
  (->Choice* (second token)))

(defparser choice< #"\s*<(.*)"
  (->Choice< (second token)))

(defparser choice- #"\s*-(.*)"
  (->Choice- (second token)))

(defparser choicet #"\s*\[(.*)"
  (->Choicet (second token)))


(defparser choices #".*"
  (loop  [token token
          tree []]
    (if (empty? token)
      tree
      (let [[token tree]
            (or-> [token tree]
              choice+
              choice&
              choice*
              choice<
              choice-
              choicet)]
        (recur token tree)))))

(defparser radio-table-question #"\z"
  (if (seq (filter (partial instance? Choice*) parse-tree))
    (RadioTableQuestion.
     ""
     ""
     (filter (partial instance? Choice+) parse-tree)
     (filter (partial instance? Choice-) parse-tree)
     (filter (partial instance? Choice*) parse-tree)
     nil)))

(defparser checkbox-table-question #"\z"
  (if (and (seq (filter (partial instance? Choice-) parse-tree))
           (seq (filter (partial instance? Choice&)) parse-tree))
    (CheckboxTableQuestion.
     ""
     ""
     (filter (partial instance? Choice+) parse-tree)
     (filter (partial instance? Choice-) parse-tree)
     (filter (partial instance? Choice&) parse-tree)
     nil)))

(defparser slider-question #"\z"
  (if (seq (filter (partial instance? Choice<) parse-tree))
    (SliderQuestion. nil nil nil nil nil nil)))

(defparser textarea-question #"\z"
  (if (seq (filter (partial instance? Choicet) parse-tree))
    (TextareaQuestion. nil nil nil nil)))

(defparser select-question #"\z"
  (if (empty? (remove (partial instance? Choice*) parse-tree))
    (SelectQuestion. nil nil nil nil)))

(defparser radio-group-question #"\z"
  (if (seq (filter (partial instance? Choice-) parse-tree))
    (RadioGroupQuestion. nil nil nil nil)))

(defparser checkbox-list-question #"\z"
  (if (seq (filter partial instance? Choice&) parse-tree)
    (CheckboxListQuestion. nil nil nil nil)))


(defparser question-block #"(?sm)^\?:.*?(?=\n*?\?:|\n+?#|\n\n|\z)"
  (let [[token question] (question [token []])]
    (or-> (choices [token []])
          radio-table-question
          checkbox-table-question
          slider-question
          textarea-question
          select-question
          radio-group-question
          checkbox-list-question)))

(defparser link #"\[(.+?)\]\(\s*(\S+)\s*?\"?(\w*?)\"?\s*\)"
  (Link. (nth token 1)
         (nth token 2)
         (nth token 3)))

(defparser breakline #"\n\n"
  (Breakline.))

(defparser text #"(?s).+?(?=\n\n|\z)"
  token)

(defparser paragraph #"(?s).*?(?=\n\n\n|\n#|\n\s*\?:|\z)"
  (loop [token token
         tree []]
    (if (empty? token)
      tree
      (let [[token tree]
            (or-> [token tree]
                  link
                  breakline
                  text)]
        (recur token tree)))))

(defparser page #"(?s)#[^#].*?(?=\n#[^#]|\z)"
  (let [[token header] (header [token []])]
    (loop [token token
           tree []]
      (if (empty? token)
        (Page. (inc (count parse-tree))
               header
               tree)
        (let [[token tree]
              (or-> [token tree]
                    heading
                    question-block
                    paragraph)]
          (recur token []))))))

(defparser title #"=+\s*(.*?)\s*=+\n*"
  (Title. (second token)))
