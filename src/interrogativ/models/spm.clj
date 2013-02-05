(ns interrogativ.models.spm
  (:require [taoensso.timbre :as log]
            [interrogativ.models.parse :as parse]
            [noir.session :as session]
            [interrogativ.views.mobile :as mobile])
  (:use [hiccup.core :only [html]]))

(def default-takk (first (parse/parse-page 1 0 "# Takk\n##Takk!")))

(defn create-header [page-id pages header]
  (mobile/header
   {:content (list [:h1 (:value header)]
                   (if-let [page-nb (re-find #"(?<=page-)\d+" page-id)]
                     (mobile/menu-button
                      {:label (format " %s / %s " page-nb pages)})))}))

(defn create-footer [prev page next]
  (mobile/footer
   {:id (format "footer-%s" (:id page))
    :content (if (and (nil? prev)
                      (not (parse/submit-page? page))
                      (nil? next))
               [:h1 " "]
               (mobile/grid-b
                {:block-a (if-not (nil? prev)
                            (mobile/left-button
                             {:link (format "#%s" (:id prev))
                              :id (if (= "ferdig" (:id page))
                                    "tilbakeinnhold")}))
                 
                 :block-c (cond (= "ferdig" (:id page))
                                (mobile/submit-button)
                                
                                (not (nil? next))
                                (mobile/right-button
                                 {:link (format "#%s" (:id next))
                                  :id (if (= "ferdig" (:id next))
                                        "tilferdig")}))}))}))

(defn create-mobile-page [pages previous-page page next-page]
  (mobile/page
   {:id (:id page)
    :header (create-header (:id page)
                           pages
                           (:header page))
    :content (mobile/content
              (when (= "ferdig" (:id page))
                [:div {:class "ikkeferdig"}])
              (map parse/hiccup (:content page)))
    :footer (create-footer previous-page
                           page
                           next-page)}))

(defn create-mobile-content [mcontent]
  (loop [content (rest mcontent)
         page (first mcontent)
         previous-page nil
         next-page (first content)
         pages []]
    (if (nil? page)
      (seq pages)
      (let [html-page (create-mobile-page (count mcontent) 
                                          previous-page
                                          page
                                          next-page)]
        (recur (rest content)
               (first content)
               page
               (second content)
               (conj pages html-page))))))

(defn create-mobile-survey [page-name pages]
  [:form {:action page-name 
          :method "post"}
   (create-mobile-content
    (assoc-in pages [(dec (count pages)) :id]
      "ferdig"))
   (mobile/page
    {:id "meny"
     :header (mobile/header
              {:content [:h1 "Meny"]})
     :content [:div {:data-role "content"
                     :data-theme "c"}
               [:p {:id "menyp"}]]})])

(defrecord Survey [survey thankyou])

(defn create-survey [page-name document]
  (let [document (parse/parse document)
        survey (:survey document)
        thankyou (:thankyou document)]
    (->Survey
     (if (parse/submit-page? (last survey))
       (mobile/layout
        {:title (:title document)
         :body (mobile/body
                (create-mobile-survey page-name survey))})
       (do (log/info "Missing submit-page for page:" page-name)
           [:h1 "Missing submit-page"]))
     (mobile/layout
      {:title (:title document)
       :body  (mobile/body
               (if-let [post (if (seq thankyou) thankyou [default-takk])]
                 (create-mobile-content post)))}))))

(defn create-survey-from [page-name markdown]
  (create-survey page-name markdown))
