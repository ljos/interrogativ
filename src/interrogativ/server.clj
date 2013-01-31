(ns interrogativ.server
  (:require [compojure.core :refer [defroutes routes ANY]]
            [compojure.handler :refer [api]]
            [compojure.route :refer [not-found resources]]
            [interrogativ.views.data :refer [data-routes]]
            [interrogativ.views.download :refer [download-routes]]
            [interrogativ.views.edit :refer [edit-routes]]
            [interrogativ.views.login :refer [login-routes]]
            [interrogativ.views.main :refer [main-routes]]
            [interrogativ.views.new :refer [new-routes]]
            [interrogativ.views.qs :refer [qs-routes]]
            [interrogativ.views.upload :refer [upload-routes]]
            [noir.cookies :refer [wrap-noir-cookies]]
            [noir.session :refer [wrap-noir-session]]
            [noir.util.middleware :refer [wrap-strip-trailing-slash]]))

(def site-routes
  (-> (routes data-routes
              download-routes
              edit-routes
              login-routes
              main-routes
              new-routes
              qs-routes
              upload-routes)
      (api)
      (wrap-noir-cookies)
      (wrap-noir-session)
      (wrap-strip-trailing-slash)))

(defroutes handler
  (ANY "*" [] site-routes)
  (resources "/"))
