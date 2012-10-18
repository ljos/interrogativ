(ns interrogativ.views.spm
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [interrogativ.models.spm :as spm]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]
        [hiccup.core :only [html]]))

(def ^{:dynamic :private} *pages* 0)

(defn create-header [page-id header]
  (common/header
   {:content (html [:h1 (:value header)]
                   [:a {:class "ui-btn-right"}
                    (format " %s / %s "
                            (re-find #"\d+" page-id)
                            *pages*)])}))

(defn create-question [question]
  (case (:question question)
    :textarea nil ;(common/textarea) doesn't exist yet
    :radio-group (common/radio-group
                  {:name (:name question)
                   :label (:label question)
                   :groups (:groups question)
                   :type (if (contains? (:options question) :horizontal)
                           "horizontal")})
    :select (common/select
             {:name (:name question)
              :label (:label question)
              :values (:values question)})
    :slider (common/slider
             {:name (:name question)
              :label (:label question)
              :max (:max question)
              :min (:min question)
              :value (:value question)})
    :radio-table (common/radio-table
                  {:name (:name question)
                   :label (:label question)
                   :sections (:sections question)
                   :values (:values question)})))

(defn create-paragraph [paragraph]
  [:p (for [item paragraph]
        (case (:type item)
          :br [:br]
          item))])

(defn create-content [content]
  (common/content
   (for [c content]
     (case (:type c)
       :heading  [(:h c) (:value c)]
       :p        (create-paragraph (:content c))
       :question (create-question c)
       :br       [:br]))))

(defn create-footer [prev page next]
  (common/footer
   {:id (format "footer-%s" (:id page))
    :content (common/grid-b
              {:block-a (if-not (or (nil? prev)
                                    (contains? (:options prev) :submit))
                          (common/left-button
                           {:link (format "#%s" (:id prev))
                            :inline "false"
                            :label "Tilbake"}))
               :block-c (cond (contains? options :submit)
                              [:input {:data-icon "arrow-r"
                                       :data-iconpos "right"
                                       :data-inline "false"
                                       :type "submit"
                                       :name "submitter"
                                       :value "Levér"}]
                              (not (nil? next)) 
                              (common/right-button
                               {:link (format "#%s" (:id next))
                                :inline "false"
                                :label "Neste"}))})}))

(defn create-document [document]
  (common/layout
   {:title (:title document)
    :body (html
           (loop [body (rest (:body document))
                  page (first (:body document))
                  previous-page nil
                  next-page (first body)
                  pages '()]
             (if (nil? page)
               (reverse pages)
               (let [html-page (common/page
                                {:id (:id page)
                                 :header (create-header (:id page)
                                                        (:header page))
                                 :content (create-content (:content page))
                                 :footer (create-footer previous-page
                                                        page
                                                        next-page)})]
                 (recur (rest body)
                        (first body)
                        page
                        (second body)
                        (cons html-page pages))))))}))

(defn create-page-from [file]
  (let [document (spm/parse file)]
    (binding [*pages* (count (:body document))]
      (create-document document))))

