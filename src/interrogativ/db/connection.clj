(ns interrogativ.db.connection
  (:require [noir.util.crypt :as crypt]
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
    (insert survey
            (values {:owner user
                     :url page
                     :markdown text
                     :survey (:survey survey)
                     :thankyou (:thankyou survey)}))))

(defn html-for-survey
  "get the html for the survey."
  [survey]
  (let [user (session/get :user)]
    (:survey
     (select surveys
             (where {:url survey
                     :owner user})))))

(defn thankyou-for-survey
  "get the html for the survey."
  [survey]
  (let [user (session/get :user)]
    (:thankyou
     (select surveys
             (where {:url survey
                     :owner user})))))

(defn markdown-for-survey
  "get the markdown for the survey"
  [survey]
  (let [user (session/get :user)]
    (:markdown
     (select surveys
             (where {:url survey
                     :owner user})))))

(defn update-survey
  "update the markdown adn html for the survey."
  [survey markdown]
  (let [user (session/get :user)
        survey (spm/create-survey survey markdown)]
    (update surveys
            (set-fields [:markdown markdown
                         :survey (:survey survey)
                         :thankyou (:post survey)])
            (where {:url [= survey]
                    :owner [= user]}))))

(defn insert-answer
  "insert new answers to database."
  [informant survey answer]
  (insert answers
    (values {:informant informant
             :survey survey
             :answer (str answer)})))

(defn answers-for-page 
  "retrieve all answers for the given page"
  [page]
  (let [user (session/get :user)]
    (map :answer
         (select answers
                 (where {:survey page})
                 (where (select surveys
                                (where {:survey page
                                        :owner user})))))))

(defn select-all-pages 
  "get all pages from database"
  []
  (let [user (session/get :user)]
    (map :url (select surveys
                      (where {:owner [= user]})))))
