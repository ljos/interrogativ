(ns interrogativ.views.spm
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [interrogativ.models.spm :as spm]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]
        [hiccup.core :only [html]]))

(def ^{:dynamic :private} *pages* 0)

(defn create-header [page]
  (common/header
   {:content (html
              [:h1 (get-in page [:header :value])]
              [:a {:class "ui-btn-right"}
               (format " %s / %s "
                       (re-find #"\d+" (:id page))
                       *pages*)])}))

(defn create-question [question]
  (case (:question question)
    :textarea nil ;(common/textarea) doesn't exist yet
    :radio-group (common/radio-group
                  {:name (:name question)
                   :label (:label question)
                   :groups (:groups question)
                   :type (if (contains? (:options question) ":horizontal")
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

(defn create-content [page]
  (common/content
   (for [content (:content page)]
     (case (:type content)
       :heading  [(:h content) (:value content)]
       :p        (create-paragraph (:content content))
       :question (create-question content)
       :br       [:br]))))

(defn create-footer [prev page next]
  (common/footer
   {:id (format "footer-%s" (:id page))
    :content (if (and (nil? next)
                      (some (partial = ":submit")
                            (:options (:header prev))))
               [:h1 " "]
               (common/grid-b
                {:block-a (if-not (or (nil? prev)
                                      (some (partial = ":submit")
                                            (:options (:header prev))))
                            (common/left-button
                             {:link (format "#%s" (:id prev))
                              :inline "false"
                              :label "Tilbake"}))
                 :block-c (cond (some (partial = ":submit")
                                      (:options (:header page)))
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
                                  :label "Neste"}))}))}))

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
                                 :header (create-header page)
                                 :content (create-content page)
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

