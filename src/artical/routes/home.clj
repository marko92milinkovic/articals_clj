(ns artical.routes.home
  (:require [compojure.core :refer :all]
            [artical.views.layout :as layout]
            [clojure.string :as str]
            [hiccup.form :refer :all]
            [hiccup.core :refer [h]]
            [ring.util.response :as ring]
            [artical.models.db :as db]))

(defn home [& [name price description error id]]
  (layout/common
    [:h1 "Articals"]
    (if (nil? id)
      (insert-form name price description error))
    (if (and id (nil? error))
      (update-artical id))
    (if (and id error)
      (update-artical id name price description error))
    (show-articals )))

(defroutes home-routes
  (GET "/" [] (home))
  (POST "/" [name price desription] (save-artical name price desription))
  (GET "/delete/:id" [id] (delete-artical id))
  (GET "/update/:id"[id] (home nil nil nil nil id))
  (POST "/saveupdate/" [id name price desription] (save-update id name price desription)))



(defn show-articals []
  [:table {:border 1}
   [:thead
    [:tr
     [:th "Id"]
     [:th "Name"]
     [:th "Price"]
     [:th {:width 200} "Description"]
     [:th "Creation time"]
     [:th "DELETE"]
     [:th "UPDATE"]]]
   (into [:tbody]
         (for [artical (db/read-articals)]
           [:tr
            [:td (:id artical)]
            [:td (:name artical)]
            [:td (:price artical)]
            [:td (:description artical)]
            [:td (format-time (:timestamp artical))]
            [:td [:a {:href (str "/delete/" (h (:id artical)))} "delete"]]
            [:td [:a {:href (str "/update/" (h (:id artical)))} "update"]]]))])

(defn save-artical [name price description]
  (cond
    (empty? name)
    (home  name price description "Enter name!")
    (empty? price)
    (home  name price description "Enter price")
    (nil? (parse-number price))
    (home  name price description "Price must be a number!")
    :else
    (do
      (db/save-artical name price description)
      (ring/redirect "/"))))

(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn delete-artical [id]
  (when-not (str/blank? id)
    (db/delete-artical id))
  (ring/redirect "/"))

(defn update-artical [id & [name price description error]]
  (if (nil? error)
    (show-artical-from-db (db/find-artical id))
    (show-artical id name price description error)))

(defn insert-form [& [name price description error]]
  (form-to [:post "/"]
           [:p "Name:"]
           (text-field "name" name)
           [:p "Price:"]
           (text-field "price" price)
           [:p "Description:"]
           (text-area {:rows 10 :cols 40} "desription" description)
           [:br]
           (submit-button "Insert")
           [:p {:style "color:red;"} error]
           [:hr]))

(defn show-artical-from-db [artical]
  (if (= nil artical)
    (ring/redirect "/")
    (form-to [:post "/saveupdate/"]
             [:p "Id:"]
             (text-field  {:readonly true} "id" (:id artical))
             [:p "Name:"]
             (text-field "name" (:name artical))
             [:p "Price:"]
             (text-field "price" (:price artical))
             [:p "Description:"]
             (text-area {:rows 10 :cols 40} "desription" (:description  artical))
             [:br]
             (submit-button "Update"))))

(defn show-artical [id name price description error]
  (form-to [:post "/saveupdate/"]
           [:p "Id:"]
           (text-field  {:readonly true} "id" id)
           [:p "Name:"]
           (text-field "name" name)
           [:p "Price:"]
           (text-field "price" price)
           [:p "Description:"]
           (text-area {:rows 10 :cols 40} "desription" description)
           [:br]
           (submit-button "Update")
           [:p {:style "color:red;"} error]))

(defn save-update [id name price description]
  (cond
    (empty? name)
    (home name price description "Enter name!" id)
    (empty? price)
    (home  name price description "Enter price" id)
    (nil? (parse-number price))
    (home  name price description "Price must be a number!" id)
    :else
    (do
      (db/update-artical id name price description)
      (ring/redirect "/"))))

(defn parse-number
  "Reads a number from a string. Returns nil if not a number."
  [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))
