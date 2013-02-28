(ns interrogativ.parser.types
  (:require [interrogativ.views.mobile :as mobile]))

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

(defprotocol Overview
  (overview [this]))

(defprotocol Keys
  (allkeys [this]))

(defrecord Paragraph [content]
  Hiccup
  (hiccup [this]
    [:p (map hiccup content)]))

(defrecord Choice+ [value])
(defrecord Choice& [value])
(defrecord Choice* [value])
(defrecord Choice< [value])
(defrecord Choice- [value])
(defrecord Choicet [value])

(defrecord TextareaQuestion [name label textarea options]
  Hiccup
  (hiccup [this]
    (mobile/textarea
     {:name name
      :label label
      :value textarea}))
  Overview
  (overview [this]
    (format "Textarea:\n%s : %s\n\n" name label))
  Keys
  (allkeys [this] #{name}))

(defrecord SelectQuestion [name label values options]
  Hiccup
  (hiccup [this]
    (mobile/select
     {:name name
      :label  label
      :values values}))
  Overview
  (overview [this]
    (with-out-str
      (println  "Select:")
      (println name ":" label)
      (doseq [idx (range (count values))]
        (println "  VAL:" (inc idx) "\tCHOICE:" (nth values idx)))
      (println)))
  Keys
  (allkeys [this]
    #{name}))

(defrecord SliderQuestion [name label min max value options]
  Hiccup
  (hiccup [this]
    (mobile/slider
     {:name name
      :label label
      :max max
      :min min
      :value value}))
  Overview
  (overview [this]
    (str "Slider:\n" name " : " label "\n  range: " min " - " max "\n\n"))
  Keys
  (allkeys [this]
    #{name}))

(defrecord CheckboxListQuestion [name label values options]
  Hiccup
  (hiccup [this]
    (mobile/checkbox-list
     {:name name
      :label label
      :values values}))
  Overview
  (overview [this]
    (with-out-str
      (println "Checkbox list:")
      (println name ":" label)
      (doseq [idx (range (count values))]
        (println (format "  %sC%02d => %s" name (inc idx) (nth values idx))))
      (println)))
  Keys
  (allkeys [this]
    (into #{} (map (partial format "%sC%02d" name)
                   (range 1 (inc (count values)))))))

(defrecord CheckboxTableQuestion [name label columns rows values options]
  Hiccup
  (hiccup [this]
    (mobile/checkbox-table
     {:name name
      :label label
      :columns columns
      :rows rows
      :values values}))
  Overview
  (overview [this]
    (with-out-str
      (println "Checkbox table:")
      (println name ":" label)
      (doseq [ridx (range (count rows))
              vidx (range (count values))]
        (println (format "  %sR%02dC%02d" name (inc ridx) (inc vidx))
                 (str "=> VAL: " (nth values vidx)
                      (if (seq columns)
                    (str "\tCOL: " (nth columns vidx))
                    "")
                      "    \tROW: " (nth rows ridx))))
      (println)))
  Keys
  (allkeys [this]
    (into #{}
          (for [ridx (range 1 (inc (count rows)))
                vidx (range 1 (inc (count values)))]
            (format "%sR%02dC%02d" name ridx vidx)))))


(defrecord RadioGroupQuestion [name label groups options]
  Hiccup
  (hiccup [this]
    (mobile/radio-list
     {:name name
      :label label
      :values groups
      :type (if (some (partial = ":horizontal") options)
              "horizontal")}))
  Overview
  (overview [this]
    (with-out-str
      (println "Radio group:")
      (println name ":" label)
      (doseq [idx (range (count groups))]
        (println "  VAL:" (inc idx) "\tLABEL:" (nth groups idx)))
      (println)))
  Keys
  (allkeys [this]
    #{name}))

(defrecord RadioTableQuestion [name label columns rows values options]
  Hiccup
  (hiccup [this]
    (mobile/radio-table
     {:name name
      :label label
      :columns columns
      :rows rows
      :values values}))
  Overview
  (overview [this]
    (with-out-str
      (println "Radio table:")
      (println name ":" label)
      (doseq [cidx (range (count columns))]
        (print (format "%s=%d  " (nth columns cidx) (inc cidx))))
      (if (seq columns) (println))
      (doseq [ridx (range (count rows))]
        (println (format "  %sR%02d => %s" name (inc ridx) (nth rows ridx)))
        (doseq [vidx (range (count values))]
          (println "    VAL:" (inc vidx) "\tLABEL:" (nth values vidx))))
      (println)))
  Keys
  (allkeys [this]
    (into #{}
          (for [ridx (range 1 (inc (count rows)))]
            (format "%sR%02d" name ridx)))))

(defrecord Header [value options])

(defrecord Page [id header content]
  Overview
  (overview [this]
    (apply str
           (map #(if (satisfies? Overview %) (overview %) "")
                content)))
  Keys
  (allkeys [this]
    (into #{} (mapcat #(if (satisfies? Keys %) (allkeys %) #{})
                      content))))

(defrecord Title [value])

(defrecord Document [title survey thankyou]
  Overview
  (overview [this]
    (apply str
           (map overview survey)))
  Keys
  (allkeys [this]
    (into (sorted-set)
          (mapcat allkeys survey))))
