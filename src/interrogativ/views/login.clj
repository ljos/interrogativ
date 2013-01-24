(ns interrogativ.views.login
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [interrogativ.views.common :as common]
            [noir.session :as session]
            [noir.util.crypt :as crypt])
  (:use [noir.core :only [defpage]]
        [noir.response :only [redirect]]))

(defpage "/login" {}
  (common/layout
   {:title "Login"
    :body (common/body           
           [:fieldset 
            [:legend "Login"]
            [:form {:action "/login"
                    :method "post"}
             [:div
              [:input {:type "text"
                       :name "uname"
                       :id "uname"
                       :placeholder "Username"}]]
             [:div
              [:input {:type "password"
                       :name "pword"
                       :id "pword"
                       :placeholder "Password"}]]
             [:div
              [:input {:class "btn btn-primary"
                       :type "submit"
                       :name "submit"
                       :id "submit"
                       :value "login"
                       :data-role "none"}]]]])}))

(defn passwd-for [user]
  (with-open [rdr (io/reader "passwd")]
    (loop [lines (line-seq rdr)]
      (cond (empty? lines)
            ""

            (re-matches (re-pattern (format "^%s:.*" user))
                        (first lines))
            (-> (first lines)
                (str/replace #"^.*?:" ""))

            :else
            (recur (rest lines))))))

(defpage [:post "/login"] {:keys [uname pword]}
  (let [encrypted (passwd-for uname)]
    (if-not (or (not (empty? encrypted))
                (crypt/compare pword encrypted))
      (redirect "/login")
      (do (session/put! :admin true)
          (redirect "/data")))))
