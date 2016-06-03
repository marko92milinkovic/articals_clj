(ns artical.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [artical.routes.home :refer [home-routes]]
            [artical.models.db :as db]))

(defn init []
  (println "artical is starting")
  (if-not (.exists (java.io.File. "./db.sq3"))
    (db/create-artical-table)))

(defn destroy []
  (println "articalapp is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))
