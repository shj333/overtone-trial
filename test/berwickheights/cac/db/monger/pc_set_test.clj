(ns berwickheights.cac.db.monger.pc-set-test
  (:require [clojure.test :refer :all]
            [berwickheights.cac.db.monger.pc-set :as db]))

(def set-name "unit-test-set-1-zzz")
(def pc-set [0 1 3])
(def octave-map [4 4 4 4  5 5 5 5  3 3 3 3])

(deftest save-pc-set
  (testing "Saving a new pc set"
    (db/save-pc-set set-name pc-set octave-map)
    (let [doc (db/get-pc-set set-name)
          {keys [name pc-set octave-map]} doc]
      (is (= set-name set-name))
      (is (= pc-set pc-set))
      (is (= octave-map octave-map))
      (db/delete-pc-set (:_id doc)))))
