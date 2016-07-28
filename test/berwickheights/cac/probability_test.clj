(ns berwickheights.cac.probability-test
  (:require [clojure.test :refer :all]
            [berwickheights.cac.probability :as prob]))

(defn- find-mean [& args]
  (let [seq (apply prob/gen-rands args)]
    (Math/round (/ (reduce + (take 100000 seq)) 1000))))

(deftest prob-test
  (testing "Uniform distributions"
    (is (= 50 (find-mean :uniform))))
  (testing "Low pass distributions"
    (is (= 33 (find-mean :low-pass 2)))
    (is (= 25 (find-mean :low-pass 3)))
    (is (= 20 (find-mean :low-pass 4)))
    (is (= 10 (find-mean :low-pass 9))))
  (testing "High pass distributions"
    (is (= 67 (find-mean :high-pass 2)))
    (is (= 75 (find-mean :high-pass 3)))
    (is (= 80 (find-mean :high-pass 4)))
    (is (= 90 (find-mean :high-pass 9))))
  (testing "Mid pass distributions"
    (is (= 50 (find-mean :mid-pass)))))
