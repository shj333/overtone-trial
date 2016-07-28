(ns berwickheights.cac.db.monger.core
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.joda-time]
            [clj-time.core :as t])
  (:import (org.bson.types ObjectId)
           (org.joda.time DateTimeZone)))


; Use UTC for all dates to/from Mongo
(DateTimeZone/setDefault DateTimeZone/UTC)

;; Tries to get the Mongo URI from the environment variable MONGOHQ_URL, otherwise default it to localhost
(defonce db (let [uri (get (System/getenv) "BHS_CAC_MONGO_URL" "mongodb://127.0.0.1/bhs_cac")
                  {:keys [db]} (mg/connect-via-uri uri)]
              db))

(defn get-document [collection-name keyword value]
  (let [document (mc/find-one-as-map db collection-name {keyword value})]
    (if-not (nil? document)
      (update-in document [:_id] str)
      nil)))

(defn save-document [collection-name document]
  (mc/insert db collection-name (merge document {:created (t/now)})))

(defn delete-document [collection-name id]
  (mc/remove-by-id db collection-name (ObjectId. id)))
