(ns berwickheights.cac.probability-test
  (:require [clojure.test :refer :all]
            [berwickheights.cac.probability :as prob]))

(defn- find-mean [& args]
  (let [seq (apply prob/gen-rands args)]
    (Math/round (/ (reduce + (take 100000 seq)) 1000))))

(deftest prob-test
  (testing "Uniform distributions"
    (is (= 50 (find-mean :uniform))))
  (testing "Min distributions"
    (is (= 33 (find-mean :min 2)))
    (is (= 25 (find-mean :min 3)))
    (is (= 20 (find-mean :min 4)))
    (is (= 10 (find-mean :min 9))))
  (testing "Max distributions"
    (is (= 67 (find-mean :max 2)))
    (is (= 75 (find-mean :max 3)))
    (is (= 80 (find-mean :max 4)))
    (is (= 90 (find-mean :max 9)))))
