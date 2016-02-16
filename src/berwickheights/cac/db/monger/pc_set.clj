(ns berwickheights.cac.db.monger.pc-set
  (:refer-clojure :exclude [sort find])
  (:require [monger.collection :as mc]
            [monger.query :as mq]
            [monger.operators :refer :all]
            [berwickheights.cac.db.monger.core :refer :all]))

(defonce ^:private collection-name* "pc_sets")

(mc/ensure-index db collection-name* (array-map :name 1) { :unique true })


(defn get-pc-sets
  []
  (let [results (mq/with-collection db collection-name*
                                    (mq/find {})
                                    (mq/fields [:name :pc-set :octave-map :created])
                                    (mq/sort (array-map :name 1)))]
    (map #(update-in % [:_id] str) results)))

(defn get-pc-set [name] (get-document collection-name* :name name))

(defn save-pc-set [name pc-set octave-map] (save-document collection-name* {:name name :pc-set pc-set :octave-map octave-map}))

(defn delete-pc-set [id] (delete-document collection-name* id))
