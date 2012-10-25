(ns interrogativ.views.spm
  (:require [interrogativ.views.common :as common]
            [interrogativ.models.data :as data]
            [interrogativ.models.spm :as spm]
            [noir.cookies :as cookies])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]
        [hiccup.core :only [html]]))

(def ^{:dynamic :private} *pages* 0)
(def ^{:dynamic :private} *submit-page* nil)

;;These atoms are here to hold the pages, this is so that we
;;can ensure that the jvm can process the questioneers instead
;;of crashing.
(def ^:private qs (atom {}))
(def ^:private submits (atom {}))

(defn submit-page? [page]
  (some (partial = ":submit")
        (get-in page [:header :options])))

(defn create-header [page]
  (common/header
   {:content (html
              [:h1 (get-in page [:header :value])]
              (if (or (re-find #"\d+" (:id page))
                      (submit-page? page))
                (common/menu-button
                 {:label (format " %s / %s "
                                 ;; Especially ugly because of this.
                                 (if (submit-page? page)
                                   *pages*
                                   (re-find #"\d+" (:id page)))
                                 *pages*)})))}))

(defn create-question [question]
  (case (:question question)
    :textarea (common/textarea
               {:name (:name question)
                :label (:label question)
                :value (:value question)})
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
                      (submit-page? prev))
               [:h1 " "]
               (common/grid-b
                {:block-a (if-not (or (nil? prev)
                                      (submit-page? prev))
                            (common/left-button
                             {:link (format "#%s" (:id prev))
                              :inline "false"
                              :label "Tilbake"}))
                 :block-c (cond (submit-page? page)
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

(defn create-pages [document]
  (loop [body (rest document)
         page (first document)
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
               (cons html-page pages))))))

(defn create-questioneer [document]
  (html
   [:form {:action *submit-page*
           :method "post"}
    (create-pages document)]
   (common/page
    {:id "menu"
     :header (common/header
              {:content [:h1 "Meny"]})
     :content (html [:div {:data-role "content"
                           :data-theme "c"}]
                    [:p {:id "menyp"}])})))

(defn create-post-page [document]
  (create-pages document))

(defn create-page-from [file]
  (let [document (spm/parse file)
        question-pages (loop [pages (:body document)]
                         (cond (empty? pages)
                               (throw (Exception.
                                       "No submit page."))
                               (submit-page? (last pages))
                               (concat (butlast pages)
                                       (list (assoc (last pages)
                                               :id "ferdig")))

                               :else
                               (recur (butlast pages))))
        post-pages (loop [pages (:body document)]
                     (cond (empty? pages)
                           (throw (Exception.
                                   "No submit page."))
                           (submit-page? (first pages))
                           (cons (assoc (second pages)
                                   :id "takk")
                                 (nnext pages))
                           :else
                           (recur (rest pages))))
        page-name (format "/%s"(re-find #".*(?=\.)" file))
        submit-page (format "%s/%s" page-name (:id (first post-pages)))]
    (binding [*pages* (count (:body question-pages))
              *submit-page* submit-page]
      (let [questioneer  (create-questioneer question-pages)
            post-page (create-post-page post-pages)]       
        (swap! qs
          assoc (keyword page-name)
            (common/layout
             {:title "title"
              :body questioneer}))
        (swap! submits
          assoc (keyword submit-page)
            (common/layout
             {:title "Takk!"
              :body post-page})) 
        (eval `(do (defpage ~submit-page []
                     (get (deref submits) ~(keyword submit-page)))
                   (defpage ~(format "%s/" submit-page) []
                     (redirect ~submit-page))))
        (eval `(do (defpage ~page-name []
                     (get (deref qs) ~(keyword page-name)))
                   (defpage ~(format "%s/" page-name) []
                     (redirect ~page-name))))))))

(create-page-from "qs/fdu.spm")
