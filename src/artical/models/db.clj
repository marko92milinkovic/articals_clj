(ns artical.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "db.sq3"})


(defn create-artical-table []
  (sql/with-connection
    db
    (sql/create-table
      :artical
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:name "TEXT"]
      [:price "REAL"]
      [:description "TEXT"]
      [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"])
    (sql/do-commands
      "CREATE INDEX timestamp_index ON guestbook (timestamp)")))


(defn read-articals []
  (sql/with-connection
    db
    (sql/with-query-results res
      ["SELECT * FROM artical ORDER BY id ASC"]
      (doall res))))


(defn save-artical [name price description]
  (sql/with-connection
    db
    (sql/insert-values
      :artical
      [:name :price :description :timestamp]
      [name price description (new java.util.Date)])))

(defn delete-artical [id]
  (sql/with-connection
    db
    (sql/delete-rows
      :artical
      ["id=?" id])))

(defn find-artical [id]
  (first
    (sql/with-connection
      db
      (sql/with-query-results res
        ["SELECT * FROM artical WHERE id= ?" id]
        (doall res)))))

(defn update-artical [id name price description]
  (sql/with-connection
    db
    (sql/update-values
      :artical
      ["id=?" id]
      {:name name :price= price :description description})))


