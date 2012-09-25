(ns interrogativ.views.common
  (:require [clojure.string :only [replace lower-case] :as string])
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-js include-css]]
        [hiccup.core :only [html]])
  (:refer-clojure :exclude [name id]))

(defpartial layout [& content]
  [:head
   [:title "Forskningsdagene UNG"]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   (include-css "http://code.jquery.com/mobile/1.2.0-rc.2/jquery.mobile-1.2.0-rc.2.min.css")
   (include-js "http://code.jquery.com/jquery-1.8.1.min.js")
   (include-js "/js/interrogativ.js")
   (include-js "http://code.jquery.com/mobile/1.2.0-rc.2/jquery.mobile-1.2.0-rc.2.min.js")
   [:style {:type "text/css"}
    ".ui-header .ui-title, .ui-footer .ui-title {
         margin-left:  0;
         margin-right: 0;
     }
     .ui-footer {
         position: absolute;
         bottom: 0px;
         width: 100%;
     }"]]
  [:body
   content])

(defpartial left-button [{:keys [name link label inline]
                          :or [inline "true" name ""]}]
  [:a {:href link
       :data-role "button"
       :data-icon "arrow-l"
       :data-inline inline}
   label])

(defpartial right-button [{:keys [link label inline id]
                           :or {inline "true" id ""}}]
  [:a {:href link
       :data-role "button"
       :id id
       :data-icon "arrow-r"
       :data-iconpos "right"
       :data-inline inline}
   label])

(defn grid-a
  ([{:keys [block-a block-b]
     :or [block-a "" block-b ""]}]
     (grid-a block-a block-b))
  ([block-a block-b & blocks]
     (html
      [:fieldset {:class "ui-grid-a"}
       [:div {:class "ui-block-a"} block-a]
       [:div {:class "ui-block-b"} block-b]
       (for [[block-a block-b] (partition 2 blocks)]
         [:div {:class "ui-block-a"} block-a]
         [:div {:class "ui-block-b"} block-b])])))

(defn grid-b
  ([{:keys [block-a block-b block-c]
     :or {block-a "" block-b "" block-c ""}}]
     (grid-b block-a block-b block-c))
  ([block-a block-b block-c & blocks]
     (html
      [:fieldset {:class "ui-grid-b"}
       [:div {:class "ui-block-a"} block-a]
       [:div {:class "ui-block-b"} block-b]
       [:div {:class "ui-block-c"} block-c]
       (for [[block-a block-b block-c] (partition 3 blocks)]
         [:div {:class "ui-block-a"} block-a]
         [:div {:class "ui-block-b"} block-b]
         [:div {:class "ui-block-c"} block-c])])))

(defpartial header [{:keys [data-position content]
                     :or {data-position ""}}]
  [:div {:data-role "header" :data-position data-position}
   content])

(defpartial footer [{:keys [data-position content id]
                     :or {data-position "" id ""}}]
  [:div {:id id :data-role "footer" :data-position data-position}
   content])

(defpartial page [{:keys [id header content footer data-title data-theme]
                   :or {id "" header "" footer "" data-title "" data-theme ""}}]
  [:div {:data-role "page" :id id :data-title data-title :data-theme data-theme}
   header
   content
   footer])

(defpartial content [& content]
  [:div {:data-role "content"} content
   [:br] [:br]])

(defpartial radio-group [{:keys [name label groups type]
                          :or {type "" id name}}]
  [:fieldset {:data-role "controlgroup" :data-type type}
   [:div {:id name
          :title label
          :class "name-holder"}
    [:legend label]
    (map-indexed (fn [idx group]
                   (let [id (format "%s-v%s" name idx)]
                     (html
                      [:input {:type "radio"
                               :name name
                               :id id
                               :value idx}]
                      [:label {:for id} group])))
                 groups)]])

;; For some reason it wants to evaluate name in this instance if
;; put in the :or part of the input.
(defpartial slider [{:keys [name label id value min max]
                     :or {min 0 max 100}}]
  [:label {:for (if id id name)} label]
  [:input {:type "range"
           :name name
           :id (if id id name)
           :value value
           :min min
           :max max}])

(defpartial select [{:keys [id name label values] :or {id name}}]
  [:div {:id name
         :title label
         :class "name-holder"}
   [:label {:for name :class "select"} label]
   [:select {:name name :id id}
    (map-indexed (fn [idx value]
                   [:option {:value idx} value])
                 values)]])

(defpartial textarea [{:keys [id name label value] :or {id name value ""}}]
  [:div {:name name
         :class "name-holder"
         :title label}
   [:label {:for name} label]
   [:textarea {:name name :id id} value]])

(defpartial radio-list [{:keys [name label values]}]
  [:fieldset {:data-role "controlgroup"}
   [:div {:id name
          :title label
          :class "name-holder"}
    [:legend label]
    (map-indexed (fn [idx value]
                   (let [id (format "%sC%s" name idx)]
                     (html [:input {:type "radio"
                                    :name name
                                    :id id
                                    :value idx}]
                           [:label {:for id} value])))
                 values)]])

(defpartial checkbox-list [{:keys [name label values]}]
  [:fieldset {:data-role "controlgroup"}
   [:legend label]
   (map-indexed (fn [idx value]
                  (let [id (format "%sC%s" name idx)
                        name (string/replace
                              (format "%sC%s" name value)
                              #"\s+"
                              "-")]
                    (html [:div {:id name
                                 :class "name-holder"
                                 :title label}
                           [:input {:type "checkbox"
                                    :name name
                                    :id id
                                    :value idx}]
                           [:label {:for id} value]])))
                values)])

(defpartial radio-table [{:keys [name label sections values]}]
  [:p label]
  [:div {:data-role "fieldcontain"}
   (for [section sections]
     [:div {:class "ui-grid-a"}
      [:div {:class "ui-block-a" :style "width:40%"}
       [:b {:style "position:relative;top:6px"} section]]
      [:div {:class "ui-block-b" :style "width:60%"}
       [:fieldset {:data-role "controlgroup"
                   :data-type "horizontal"}
        (let [name (string/replace
                    (string/replace
                     (format "%sC%s" name section)
                     #"[øåæé,/]"
                     "")
                    #"\s+"
                    "-")]
          [:div {:style "text-align:right"
                 :id name
                 :title (str section ": " label)
                 :class "name-holder"}
           (map-indexed
            (fn [value label]
              (let [id (format "%sC%s"
                               (string/replace section
                                 #"\s+" "")
                               value)]
                (html [:input {:type "radio"
                               :name name
                               :id id
                               :value value}]
                      [:label {:for id} label])))
            values)])]]])])
