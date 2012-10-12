(ns interrogativ.views.data
  (:require [interrogativ.models.data :as data]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:use [noir.core :only [defpage pre-route]]
        [noir.response :only [redirect content-type]]
        [hiccup.core :only [html]]))

(defpage "/login" {}
  (html
   [:head
    [:title "Login"]]
   [:body
    [:form {:action "/login" :method "post"}
     [:div
      [:label {:for "uname"} "Username:"]
      [:input {:type "text" :name "uname" :id "uname" :value "" :data-role "none"}]]
     [:div
      [:label {:for "pword"} "Password:"]
      [:input {:type "password" :name "pword" :id "pword" :value "" :data-role "none"}]]
     [:div
      [:input {:type "submit"
               :name "submit"
               :id "submit"
               :value "login"
               :data-role "none"}]]]]))

(defn passwd-for [user]
  (with-open [rdr (io/reader "passwd")]
    (loop [lines (line-seq rdr)]
      (cond (empty? lines)
            ""
            (re-matches (re-pattern (format "^%s:.*" user))
                        (first lines))
            ,(str/replace (first lines) #"^.*?:" "")
            :else
            ,(recur (rest lines))))))

(defpage [:post "/login"] {:keys [uname pword]}
  (let [encrypted (passwd-for uname)]
    (if-not (and (not (empty? encrypted))
                 (crypt/compare pword encrypted))
      (redirect "/login")
      (do (session/put! :admin true)
          (redirect "/data")))))

(pre-route "/data" {}
           (if-not (session/get :admin)
             (redirect "/login")))

(pre-route "/data/*" {}
           (if-not (session/get :admin)
             (redirect "/login")))

(defpage "/data/" {}
  (redirect "/data"))

(defpage "/data" {}
  (html (for [f (.listFiles (java.io.File. "db/"))
              :when (not (.isDirectory f))
              :let [name (str/replace (.getName f) #"\.dat$" ".csv")]]
          [:div [:a {:href (format "/data/%s" name)} name] [:br]])))

(defpage "/data/:file" {:keys [file]}
  (content-type "text/csv"
                (data/create-csv-from-file
                 (format "db/%s"
                         (str/replace
                          file #"\.csv$" ".dat")))))
