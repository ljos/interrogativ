(ns interrogativ.models.types
  (:require [interrogativ.views.mobile :as mobile])
  (:use [hiccup.core :only [html]]))

(defprotocol Hiccup
  (hiccup [this]))

(defrecord Header [value options]
  Hiccup
  (hiccup [this] [:h1 value]))

(defrecord Heading [size value]
  Hiccup
  (hiccup [this] [size value]))

(defrecord SelectQuestion [name label options values]
  Hiccup
  (hiccup [this]
    (mobile/select
     {:name name
      :label [:h4 label]
      :values values})))

(defrecord RadioTableQuestion [name label options sections values]
  Hiccup
  (hiccup [this]
    (mobile/radio-table
     {:name name
      :label [:h4 label]
      :sections sections
      :values values})))

(defrecord SliderQuestion [name label options min mac value]
  Hiccup
  (hiccup [this]
    (mobile/slider
     {:name name
      :label [:h4 label]
      :max max
      :min min
      :value value})))

(defrecord TextareaQuestion [name label options textarea]
  Hiccup
  (hiccup [this]
    (mobile/textarea
     {:name name
      :label label
      :value textarea})))

(defrecord RadioGroupQuestion [name label options groups]
  Hiccup
  (hiccup [this]
    (mobile/radio-group
     {:name name
      :label [:h4 label]
      :groups groups
      :type (if (contains? options ":horizontal")
              "horizontal")})))

(defrecord Breakline []
  Hiccup
  (hiccup [this] [:br]))

;; String is extended with MobileHTML so that we don't have to
;; guard for strings in Parahraphs or other places. The
;; extension should not be used outside of this namespace.
(extend-type String
  Hiccup
  (hiccup [this] this))

(defrecord Paragraph [content]
  Hiccup
  (hiccup [this] [:p (map hiccup content)]))

(defprotocol MobileHTMLPage
  (page [this prev next]))

(defn submit-page? [page]
  (some (partial = ":submit")
        (get-in page [:header :options])))

(defn- create-footer [prev page next]
  (mobile/footer
   {:id (format "footer-%s" (:id page))
    :content (if (and (nil? next)
                      (submit-page? prev))
               [:h1 " "]
               (mobile/grid-b
                {:block-a (if-not (or (nil? prev)
                                      (submit-page? prev))
                            (mobile/left-button
                             {:link (format "#%s" (:id prev))
                              :id (if (= "ferdig" (:id page))
                                    "tilbakeinnhold")}))
                 
                 :block-c (cond (submit-page? page)
                                (mobile/submit-button)
                                
                                (not (nil? next))
                                (mobile/right-button
                                 {:link (format "#%s" (:id next))
                                  :id (if (= "ferdig" (:id next))
                                        "tilferdig")}))}))}))

(defrecord Page [id header content]
  MobileHTMLPage
  (page [this prev next]
    (html
     (mobile/page
      {:id id
       :header (mobile/header
                {:content (html (mobileHTML header)
                                (if id
                                  (mobile/menu-button
                                   {:label "Menu"})))})
       :content (map mobileHTML content)
       :footer (create-footer prev next this)}))))

(defprotocol MobileHTMLDocument
  (document [this submit-page]))

(defrecord Document [title body]
  MobileHTMLDocument
  (document [this submit-page]
    (html
     [:form {:action submit-page
             :method "post"}
      (map page body)
      (mobil/page
       {:id "meny"
        :header (mobile/header
                 {:content [:h1 "Meny"]})
        :content [:div {:data-role "content"
                        :data-theme "c"}
                  [:p {:id "menyp"}]]})])))


