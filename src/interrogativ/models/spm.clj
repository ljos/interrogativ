(ns interrogativ.models.spm
  (:require [clojure.tools.logging :as log]
            [interrogativ.models.data :as data]
            [interrogativ.models.parse :as parse]
            [interrogativ.views.mobile :as mobile])
  (:use [hiccup.core :only [html]]))

(defn create-header [page-id pages header]
  (mobile/header
   {:content (list [:h1 (:value header)]
                   (if-let [page-nb (re-find #"\d+" page-id)]
                     (mobile/menu-button
                      {:label (format " %s / %s " page-nb pages)})))}))

(defn create-footer [prev page next]
  (mobile/footer
   {:id (format "footer-%s" (:id page))
    :content (if (and (nil? prev) (nil? next))
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
  [:form {:action (str page-name "/takk")
          :method "post"}
   (create-mobile-content
    (assoc pages
      (dec (count pages))
      (assoc (last pages)
        :id "ferdig")))
   (mobile/page
    {:id "meny"
     :header (mobile/header
              {:content [:h1 "Meny"]})
     :content [:div {:data-role "content"
                     :data-theme "c"}
               [:p {:id "menyp"}]]})])

(defrecord Survey [survey post])

(defn create-survey [page-name document]
  (let [survey (:survey document)
        post   (:post document)]
    (->Survey
     (if (parse/submit-page? (last survey))
       (mobile/layout
        {:title (:title document)
         :body (mobile/body
                (create-mobile-survey page-name survey))})
       (do (log/info "Missing submit-page for page:" page-name)
           (html [:h1 "Missing submit-page"])))
     (mobile/layout
      {:title (:title document)
       :body  (mobile/body
               (if (seq post)
                 (create-mobile-content post)
                 [:h1 "Takk!"]))}))))

(defn create-survey-from [file]
  (log/info "Create page from file:" file)
  (let [page-name (format "/%s" (re-find #".*?(?=\.|\z)" file))
        document (parse/parse file)
        survey  (create-survey page-name document)]
    (data/create-store page-name)
    (data/store-survey page-name survey)))
