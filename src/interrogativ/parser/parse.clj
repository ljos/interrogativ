(ns interrogativ.parser.parse
  (:require [clojure.string :as str]
            [interrogativ.parser.types :refer :all])
  (:refer-clojure :exclude [newline]))

(defmacro deftokenizer
  "Builds a parser given a name, a return type and
a tag for the beginning of the expression and a
vector of end tags"
  [name pretag posttags]
  `(defn ~name [[document# tokens#]]
     (letfn [(pre# [[document# tokens#]]
               (let [n# (count ~pretag)]
                 (if (= (seq ~pretag)
                        (seq (take n# document#)))
                   [(drop n# document#)
                    (conj tokens#
                          {:type '~name
                           :value ""})])))
             (post# [[document# tokens#]]
               (loop [document# document#
                      tokens# tokens#]
                 (let [posttag# (filter
                                 (fn [tag#]
                                   (= (seq tag#)
                                      (seq (take (count tag#)
                                                 document#))))
                                 (if (string? ~posttags)
                                   (list ~posttags)
                                   ~posttags))]
                   (cond (seq posttag#)
                         [(drop (dec (count (first posttag#)))
                                document#)
                          tokens#]

                         (empty? document#)
                         [document# tokens#]

                         :else
                         (recur (rest document#)
                                (update-in tokens#
                                  [(dec (count tokens#)) :value]
                                  (fn [s#] (str s# (first document#)))))))))]
       (some-> [document# tokens#] pre# post#))))

(deftokenizer link  "[" ")")
(deftokenizer heading "##" "\n")
(deftokenizer paragraph "" ["\n\n\n" "\n#"])
(deftokenizer question "?:" "\n\n")
(deftokenizer header "#" "\n")
(deftokenizer title "===" "===\n")
(deftokenizer newline "\n" "")

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

(defn parse-document
  "Tokenize the document"
  ([document]
     (tokenize document []))
  ([document tokens]
     (if-not (seq document)
       tokens
       (let [[document tokens]
             (or-> [document tokens]
                   page
                   title)]
         (recur document tokens)))))
