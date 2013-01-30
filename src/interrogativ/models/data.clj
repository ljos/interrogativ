(ns interrogativ.models.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [noir.util.crypt :as crypt]
            [noir.session :as session]
            [interrogativ.models.spm :as spm])
  (:use [korma db core]))

(defdb db (sqlite3 {:db "data.sqlite"}))

(defentity users)
(defentity surveys)
(defentity answers)

(defn insert-user 
  "insert user to database"
  [user password]
  (insert users
    (values {:username user
             :password (crypt/encrypt password)})))

(defn remove-user 
  "removes a user from the database."
  [user]
  (delete users
    (where {:username user})))

(defn valid-user?
  "Checks if the user trying to log has a valid password."
  [user password]
  (if-let [encrypted (-> (select users
                           (where {:username [= user]}))
                         first
                         :password)]
    (crypt/compare password
                   encrypted)))

(defn insert-survey 
  "insert new survey to database."
  [page text]
  (let [user (session/get :user)
        survey (spm/create-survey (str "/qs/" page) text)]
    (insert surveys
            (values {:owner user
                     :url page
                     :markdown text
                     :survey (:survey survey)
                     :thankyou (:thankyou survey)}))))

(defn html-for-survey
  "get the html for the survey."
  [page]
  (log/info "html for survey" page)
  (:survey
   (first
    (select surveys
            (where {:url page})))))

(defn thankyou-for-survey
  "get the html for the survey."
  [survey]
  (:thankyou
   (first
    (select surveys
            (where {:url survey})))))

(defn markdown-for-survey
  "get the markdown for the survey"
  [survey]
  (let [user (session/get :user)]
    ((comp :markdown first)
     (select surveys
             (where {:url survey
                     :owner user})))))

(defn update-survey
  "update the markdown adn html for the survey."
  [survey markdown]
  (let [user (session/get :user)
        page (spm/create-survey (str "/qs/" survey) markdown)]
    (update surveys
            (set-fields {:markdown markdown
                         :survey (:survey page)
                         :thankyou (:thankyou page)})
            (where {:url survey
                    :owner user}))))


(defn answers-for-page 
  "retrieve all answers for the given page"
  [page]
  (let [user (session/get :user)]
    (map (comp read-string :answer)
         (select answers
           (where {:survey page})
           (join surveys (= :answers.survey :surveys.url))
           (where {:surveys.owner [= user]})))))

(defn select-all-pages 
  "get all pages from database"
  []
  (let [user (session/get :user)]
    (map :url (select surveys
                      (where {:owner [= user]})))))


(defn survey-for-name [name]
  (html-for-survey name))
(defn insert-answer
  "insert new answers to database."
  [survey answer]
  (insert answers
    (values {:survey survey
             :answer (str answer)})))

(defn thankyou-for-name [name]
  (thankyou-for-survey name))

(defn store-answer
  "store the answer given to the survey"
  [page answer]
  (-> (insert-answer page answer)
       vals
       first
       (+ 100000)
       (Long/toString 16)))
  []

(defn create-csv-for-page 
  "creates a csv for the answers given to survey on page"
  [page]
  (let [submissions (answers-for-page page)
        keys (into (sorted-set) (mapcat keys submissions))]
    (log/info "Create csv for page: " page)
    (with-out-str 
      (println (str/join "," (map name keys)))
      (doseq [submission submissions]
        (println (str/join ","
                           (map (partial format "\"%s\"")
                                (for [key keys]
                                  (get submission key -1)))))))))

(defn markdown-for-page 
  "retrives the markdown from the database"
  [page]
  (markdown-for-survey page))

(defn upload-file [{:keys [filename tempfile]}]
  (log/info (format "Storing file qs/%s" filename))
  (insert-survey (str/replace filename #"\..*$" "")
                    (slurp tempfile)))

(defn pages []
  (select-all-pages))

(defn owner? 
  "Checks if the current session is the owner of the page."
  [page]
  (let [user (session/get :user)]
    (not-empty (select surveys
                       (where {:url page
                               :owner user})))))
