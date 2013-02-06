(ns interrogativ.models.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
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

(defn survey
  "get the html for the survey."
  [page]
  (log/info "html for survey" page)
  (:survey
   (first
    (select surveys
      (where {:url page})))))

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

(defn thankyou
  "get the html for the survey."
  [survey]
  (:thankyou
   (first
    (select surveys
      (where {:url survey})))))

(defn markdown
  "get the markdown for the survey"
  [survey]
  (let [user (session/get :user)]
    ((comp :markdown first)
     (select surveys
       (where {:url survey
               :owner user})))))

(defn submissions
  "retrieve all submissions for the given page on the date"
  [page date]
  (let [user (session/get :user)]
    (map #(merge {"informant" (Long/toString (+ 100000 (:id %)) 16)}
                 (read-string (:answer %)))
         (select answers
           (where {:survey page
                   (sqlfn date :answered) date})
           (join surveys (= :answers.survey :surveys.url))
           (where {:surveys.owner user})))))

(defn insert-answer
  "insert new answers to database."
  [survey answer]
  (insert answers
    (values {:survey survey
             :answer (str answer)})))


(defn store-answer
  "store the answer given to the survey"
  [page answer]
  (-> (insert-answer page answer)
       vals
       first
       (+ 100000)
       (Long/toString 16)))

(defn pages
  "get all pages from database that belongs to the current user."
  []
  (let [user (session/get :user)]
    (apply sorted-set-by
           (fn [f s] (let [f (if (keyword f) (name f) f)
                          s (if (keyword s) (name s) s)]
                      (> 0 (compare f s))))
           (map :url (select surveys
                       (where {:owner user}))))))

(defn dates
  "get the dates for the page that have answers"
  [page]
  (let [user (session/get :user)]
    (map :date
         (select answers
           (modifier "distinct")
           (fields  [(sqlfn date :answered) :date])
           (where {:survey page})
           (join surveys (= :answers.survey :surveys.url))
           (where {:surveys.owner user})))))

(defn create-csv
  "creates a csv for the answers given to survey on page on date"
  [page date]
  (let [submissions (submissions page date)
        keys (apply sorted-set-by
                    (fn [f s] (let [f (if (keyword f) (name f) f)
                                   s (if (keyword s) (name s) s)]
                               (> 0 (compare f s))))
           (mapcat keys submissions))]
    (log/info "Create csv for page: " page)
    (with-out-str 
      (println (str/join "," (map name keys)))
      (doseq [submission submissions]
        (println (str/join ","
                           (map (partial format "\"%s\"")
                                (for [key keys]
                                  (get submission key -1)))))))))

(defn upload-file
  "takes a map with filename and a io.File to insert into database"
  [{:keys [filename tempfile]}]
  (log/info (format "Storing file qs/%s" filename))
  (insert-survey (str/replace filename #"\..*$" "")
                 (slurp tempfile)))

(defn owner? 
  "Checks if the current session is the owner of the page."
  [page]
  (let [user (session/get :user)]
    (not-empty (select surveys
                 (where {:url page
                         :owner user})))))
