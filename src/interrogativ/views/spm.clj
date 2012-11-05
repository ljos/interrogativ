(ns interrogativ.views.spm
  (:require [interrogativ.views.mobile :as mobile]
            [interrogativ.models.data :as data]
            [interrogativ.models.spm :as spm]
            [clojure.tools.logging :as log]
            [noir.cookies :as cookies])
  (:import [java io.File])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]
        [hiccup.core :only [html]]
        [slingshot.slingshot :only [try+ throw+]]))

(def ^{:dynamic :private} *pages* 0)
(def ^{:dynamic :private} *submit-page* nil)

;;These atoms are here to hold the pages, this is so that we
;;can ensure that the jvm can process the questioneers instead
;;of crashing.
(def qs (atom {}))
(def submits (atom {}))

(defn submit-page? [page]
  (some (partial = ":submit")
        (get-in page [:header :options])))

(defn create-header [page]
  (mobile/header
   {:content (html
              [:h1 (get-in page [:header :value])]
              ;; This is really ugly, need to fix.
              (if (or (re-find #"\d+" (:id page))
                      (submit-page? page))
                (mobile/menu-button
                 {:label (format " %s / %s "
                                 ;; Especially ugly because of this.
                                 (if (submit-page? page)
                                   *pages*
                                   (re-find #"\d+" (:id page)))
                                 *pages*)})))}))

(defn create-question [question]
  (case (:question question)
    :textarea (mobile/textarea
               {:name (:name question)
                :label (:label question)
                :value (:value question)})
    :radio-group (mobile/radio-group
                  {:name (:name question)
                   :label [:h4 (:label question)]
                   :groups (:groups question)
                   :type (if (contains? (:options question)
                                        ":horizontal")
                           "horizontal")})
    :select (mobile/select
             {:name (:name question)
              :label  [:h4 (:label question)]
              :values (:values question)})
    :slider (mobile/slider
             {:name (:name question)
              :label [:h4 (:label question)]
              :max (:max question)
              :min (:min question)
              :value (:value question)})
    :radio-table (mobile/radio-table
                  {:name (:name question)
                   :label [:h4 (:label question)]
                   :sections (:sections question)
                   :values (:values question)})))

(defn create-paragraph [paragraph]
  [:p (for [item paragraph]
        (case (:type item)
          :br [:br]
          item))])

(defn create-content [page]
  (mobile/content
   (if (submit-page? page)
     [:div {:class "ikkeferdig"}])
   (for [content (:content page)]
     (case (:type content)
       :heading  [(:h content) (:value content)]
       :p        (create-paragraph (:content content))
       :question (create-question content)
       :br       [:br]))))

(defn create-footer [prev page next]
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
                                    "tilbakeinnhold")
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
                                (mobile/right-button
                                 {:link (format "#%s" (:id next))
                                  :id (if (= "ferdig" (:id next))
                                        "tilferdig")
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
      (let [html-page (mobile/page
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
    (create-pages document)
    (mobile/page
     {:id "meny"
      :header (mobile/header
               {:content [:h1 "Meny"]})
      :content [:div {:data-role "content"
                      :data-theme "c"}
                [:p {:id "menyp"}]]})]))

(defn create-post-page [document]
  (create-pages document))

(defn create-page-from [file]
  (log/info "Create page from file:" file)
  (let [page-name (format "/%s"(re-find #".*?(?=\.|\z)" file))]
    (try+
     (let [document (spm/parse file)
           question-pages (loop [pages (:body document)]
                            (cond (empty? pages)
                                  (throw+ page-name)
                                  (submit-page? (last pages))
                                  (concat (butlast pages)
                                          (list (assoc (last pages)
                                                  :id "ferdig")))

                                  :else
                                  (recur (butlast pages))))
           post-pages (loop [pages (:body document)]
                        (cond (empty? pages)
                              (throw+ page-name)
                              (submit-page? (first pages))
                              (cons (assoc (second pages)
                                      :id "takk")
                                    (nnext pages))
                              :else
                              (recur (rest pages))))
           submit-page (format "%s/%s"
                               page-name
                               (:id (first post-pages)))]
       (binding [*pages* (count question-pages)
                 *submit-page* submit-page]
         (let [questioneer  (create-questioneer question-pages)
               post-page (create-post-page post-pages)]
           (data/create-store page-name)
           (swap! qs
             assoc (keyword page-name)
             (mobile/layout
              {:title (:title document)
               :body (mobile/body
                      questioneer)}))
           (swap! submits
             assoc (keyword submit-page)
             (mobile/layout
              {:title "Takk!"
               :body (mobile/body
                      post-page)}))
           (eval `(do
                    (defpage [:post ~submit-page] ~'data
                      (let [~'submitter-id (data/generate-submitter-id)]
                        (cookies/put! :tracker {:value ~'submitter-id
                                                :path ~page-name
                                                :expires 1
                                                :max-age 86400})
                        (data/store-answer
                         (-> ~'data
                             (dissoc :submitter)
                             (assoc :informant ~'submitter-id))
                         ~page-name)
                        (redirect ~submit-page)))
                    (defpage ~submit-page []
                      (get (deref submits) ~(keyword submit-page)))
                    (defpage ~(format "%s/" submit-page) []
                      (redirect ~submit-page))))
           (eval `(do
                    (defpage ~page-name []
                      (get (deref qs) ~(keyword page-name)))
                    (defpage ~(format "%s/" page-name) []
                      (redirect ~page-name)))))))
     (catch String page-name
       (log/info "Missing submit-page for file:" file)
       (data/create-store page-name)
       (eval `(defpage ~page-name []
                (html [:h1 "Missing submit-page"]))))
     (catch Exception e
       (log/error "Unexcepted exception cought: " e)
       (.printStackTrace e)
       (data/create-store page-name)
       (eval `(defpage ~page-name []
                (html
                 [:h1 (str "Unexpected exception."
                           "See log for more information.")])))))))