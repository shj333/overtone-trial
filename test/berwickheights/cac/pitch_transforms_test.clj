(ns berwickheights.cac.pitch-transforms-test
  (:require [clojure.test :refer :all]
            [berwickheights.cac.pitch-transforms :as pt]))

(def example-multi-src-set [:Ab :C :A :B])
(def example-multi-parts [[:D :F :Eb] [:Bb :Db] example-multi-src-set [:E :G] [:Gb]])
(defn boulez-example [set transpose-level]
  (-> (pt/boulez-multi set example-multi-src-set)
      (pt/transform transpose-level pt/transpose)
      pt/named-set))

(deftest boulez-multi-test
  (testing "Boulez multiplication not working"
    (let [expected '((:Ab :B :A :C :Eb :Db :A :C :Bb :B :D :C)
                      (:E :G :Ab :B :F :Ab :G :Bb)
                      (:D :Gb :Eb :F :Gb :Bb :G :A :Eb :G :E :Gb :F :A :Gb :Ab)
                      (:Bb :Db :D :F :B :D :Db :E)
                      (:C :E :Db :Eb))
          actual (map boulez-example example-multi-parts (repeat 6))]
      (is (= expected actual)))
    (let [expected '((:B :D :C :Eb :Gb :E :C :Eb :Db :D :F :Eb)
                      (:G :Bb :B :D :Ab :B :Bb :Db)
                      (:F :A :Gb :Ab :A :Db :Bb :C :Gb :Bb :G :A :Ab :C :A :B)
                      (:Db :E :F :Ab :D :F :E :G)
                      (:Eb :G :E :Gb))
          actual (map boulez-example example-multi-parts (repeat 9))]
      (is (= expected actual)))
    (let [expected '((:A :C :Bb :Db :E :D :Bb :Db :B :C :Eb :Db)
                      (:F :Ab :A :C :Gb :A :Ab :B)
                      (:Eb :G :E :Gb :G :B :Ab :Bb :E :Ab :F :G :Gb :Bb :G :A)
                      (:B :D :Eb :Gb :C :Eb :D :F)
                      (:Db :F :D :E))
          actual (map boulez-example example-multi-parts (repeat 7))]
      (is (= expected actual)))))

(deftest transposition
  (testing "Transposition not working"
        (let [expected '(5 6 8 9 10 11)
              actual (pt/transform [0 1 3 4 5 6] 5 pt/transpose)]
          (is (= expected actual)))))





