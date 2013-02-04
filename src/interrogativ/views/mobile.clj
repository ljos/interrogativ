(ns interrogativ.views.mobile
  (:require [clojure.string :only [replace lower-case] :as str]
            [interrogativ.views.common :as common])
  (:use [hiccup.core :only [html]]
        [hiccup.page :only [include-js include-css]])
  (:refer-clojure :exclude [name]))

(defn body [& content]
  (common/body content))

(defn layout [{:keys [title body]}]
  (html
   [:head
    [:title title]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    (include-css
     (str common/jquery "mobile/1.2.0/jquery.mobile-1.2.0.min.css"))
    (include-js
     (str common/jquery "jquery-1.8.2.min.js"))
    (include-js
     (str common/jquery "mobile/1.2.0/jquery.mobile-1.2.0.min.js"))
    (include-js "/cljs/mobile.js")
    (include-css "/css/mobile.css")]
   body))

(defn left-button [{:keys [id link label inline]
                    :or {label "Tilbake" inline "false" id nil}}]
  [:a {:href link
       :id id
       :data-role "button"
       :data-icon "arrow-l"
       :data-inline inline}
   label])

(defn right-button [{:keys [id link label inline]
                     :or {label "Neste" inline "false" id nil}}]
  [:a {:href link
       :data-role "button"
       :id id
       :data-icon "arrow-r"
       :data-iconpos "right"
       :data-inline inline}
   label])

(defn submit-button []
  [:input {:data-icon "arrow-r"
           :data-iconpos "right"
           :data-inline "false"
           :type "submit"
           :name "submitter"
           :value "Levér"}])

(defn menu-button [{:keys [label]}]
  [:a {:class "ui-btn-right"
       :href "#meny"
       :data-icon "gear"
       :data-iconpos "right"
       :data-rel "dialog"
       :data-transition "slidedown"}
   label])

(defn grid-a
  ([{:keys [block-a block-b]
     :or [block-a "" block-b ""]}]
     (grid-a block-a block-b))
  ([block-a block-b & blocks]
     [:fieldset {:class "ui-grid-a"}
      [:div {:class "ui-block-a"} block-a]
      [:div {:class "ui-block-b"} block-b]
      (for [[block-a block-b] (partition 2 blocks)]
        (list [:div {:class "ui-block-a"} block-a]
              [:div {:class "ui-block-b"} block-b]))]))

(defn grid-b
  ([{:keys [block-a block-b block-c]
     :or {block-a "" block-b "" block-c ""}}]
     (grid-b block-a block-b block-c))
  ([block-a block-b block-c & blocks]
     [:fieldset {:class "ui-grid-b"}
      [:div {:class "ui-block-a"} block-a]
      [:div {:class "ui-block-b"} block-b]
      [:div {:class "ui-block-c"} block-c]
      (for [[block-a block-b block-c] (partition 3 blocks)]
        (list [:div {:class "ui-block-a"} block-a]
              [:div {:class "ui-block-b"} block-b]
           [:div {:class "ui-block-c"} block-c]))]))

(defn header [{:keys [data-position content data-theme]
                     :or {data-position nil data-theme "a"}}]
  [:div {:data-role "header"
         :data-position data-position
         :data-theme data-theme}
   content])

(defn footer [{:keys [data-position content id]
               :or {data-position nil id nil}}]
  [:div {:id id :data-role "footer" :data-position data-position}
   content])

(defn page [{:keys [id header content footer data-title data-theme]
             :or {id nil header nil footer nil data-title nil data-theme nil}}]
  [:div {:data-role "page"
         :id id
         :data-title data-title
         :data-theme data-theme}
   header
   content
   footer])

(defn content [& content]
  [:div {:data-role "content"} content
   [:br] [:br]])

(defn radio-group [{:keys [name label groups type]
                    :or {type nil id name}}]
  [:fieldset {:data-role "controlgroup" :data-type type}
   [:legend label]
   (apply concat
          (map-indexed (fn [idx group]
                         (let [id (format "%sC%02d" name (inc idx))]
                           (list [:input {:type "radio"
                                          :name name
                                          :id id
                                          :value idx}]
                                 [:label {:for id} group])))
                       groups))])

;; For some reason it wants to evaluate name in this instance if
;; put in the :or part of the input.
(defn slider [{:keys [name label id value min max]
               :or {min 0 max 100}}]
  (list [:label {:for (if id id name)} label]
        [:input {:type "range"
                 :name name
                 :id (if id id name)
                 :value value
                 :min min
                 :max max}]))

(defn select [{:keys [id name label values] :or {id name}}]
  (list [:label {:for name :class "select"} label]
        [:select {:name name :id id}
         (map-indexed (fn [idx value]
                        [:option {:value (inc idx)} value])
                      values)]))


(defn textarea [{:keys [id name label value]
                 :or {id name value ""}}]
  (list [:label {:for name} label]
        [:textarea {:name name :id id} value]))

(defn radio-list [{:keys [name label values]}]
  [:fieldset {:data-role "controlgroup"}
   [:legend label]
   (map-indexed (fn [idx value]
                  (let [id (format "%sC%02d" name (inc idx))]
                    (list [:input {:type "radio"
                                   :name name
                                   :id id
                                   :value idx}]
                          [:label {:for id} value])))
                values)])

(defn checkbox-list [{:keys [name label values]}]
  [:fieldset {:data-role "controlgroup"}
   [:legend label]
   (map-indexed (fn [idx value]
                  (let [id (format "%sC%02d" name (inc idx))
                        name (-> (format "%sC%02d" name (inc idx))
                                 (str/replace #"\s+" "-"))]
                    (list [:input {:type "checkbox"
                                   :name name
                                   :id id
                                   :value 1}]
                          [:label {:for id} value])))
                values)])

(defn table [{:keys [name type label columns rows values]}]
  [:div {:data-role "fieldcontain"}
   [:fieldset {:data-role "controlgroup"
               :data-type "horizontal"}
    [:p [:legend label]]
    [:table
     [:tr
      [:th ""]
      (for [column columns]
        [:th column])]
     (map-indexed
      (fn [idx row]
        (let [name (format "%sR%02d" name (inc idx))]
          [:tr
           [:td row]
           (map-indexed
            (fn [idx label]
              (let [id (format "%sC%02d" name (inc idx))]
                [:td
                 [:input {:type type
                          :name (if (= type "checkbox") id name)
                          :id id
                          :value (if (= "checkbox" type) 1 (inc idx))}]
                 [:label {:for id} label]]))
            values)]))
      rows)]]])

(defn checkbox-table [{:keys [name label columns rows values]}]
  (table
   {:name name
    :type "checkbox"
    :label label
    :columns columns
    :rows rows
    :values values}))

(defn radio-table [{:keys [name label columns rows values]}]
  (table
   {:name name
    :type "radio"
    :label label
    :columns columns
    :rows rows
    :values values}))
