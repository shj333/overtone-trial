(ns berwickheights.cac.pitch-transforms-test
  (:require [clojure.test :refer :all]
            [berwickheights.cac.pitch-transforms :as pt]))

(def test-set [0 1 3 4 5 6])

(deftest named-sets
  (testing "Numeric to named sets"
    (is (= '(:C :Db :Eb :E :F :Gb) (pt/named-set test-set))))
  (testing "Named to numeric sets"
    (is (= (pt/numeric-set '(:C :Db :Eb :E :F :Gb)) test-set)))
  (testing "Multiple operations"
    (is (= (pt/numeric-set (pt/named-set test-set)) test-set))))

(deftest transposition
  (testing "Transposition of pc sets"
    (let [expected '(5 6 8 9 10 11)
          actual (pt/transform test-set 5 pt/transpose)]
      (is (= expected actual)))
    (let [expected '((1 2 4 5 6 7) (3 4 6 7 8 9) (5 6 8 9 10 11))
          actual (pt/transform-many test-set [1 3 5] pt/transpose)]
      (is (= expected actual)))))

(deftest inversion
  (testing "Inversion of pc sets"
    (let [expected '(0 11 9 8 7 6)
          actual (pt/transform test-set 0 pt/invert)]
      (is (= expected actual)))
    (let [expected '((1 0 10 9 8 7) (3 2 0 11 10 9) (5 4 2 1 0 11))
          actual (pt/transform-many test-set [1 3 5] pt/invert)]
      (is (= expected actual)))))

(deftest retrograde
  (testing "Retrograde of pc sets"
    (let [expected '(6 5 4 3 1 0)
          actual (pt/retro test-set 0)]
      (is (= expected actual)))
    (let [expected '(8 7 6 5 3 2)
          actual (pt/retro test-set 2)]
      (is (= expected actual)))
    (let [expected '((6 5 4 3 1 0) (8 7 6 5 3 2))
          actual (pt/retro-many test-set [0 2])]
      (is (= expected actual)))))

(def example-multi-src-set [:Ab :C :A :B])
(def example-multi-parts [[:D :F :Eb] [:Bb :Db] example-multi-src-set [:E :G] [:Gb]])
(defn boulez-example [set transpose-level]
  (-> (pt/boulez-multi set example-multi-src-set)
      (pt/transform transpose-level pt/transpose)
      pt/named-set))

(deftest boulez-multi-test
  (testing "Boulez multiplication"
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

(def octave-map [1 2 3 4 5 6 7 8 1 2 3 4])
(deftest map-pcs-to-pitches-test
  (testing "Mapping pc sets to pitches"
    (let [expected '(:C1 :Db2 :Eb4 :E5 :F6 :Gb7)
          actual (pt/map-pcs-to-pitches test-set octave-map)]
      (is (= expected actual)))))

(deftest interval-test
  (testing "Finding intervals in pc set"
    (let [expected '(3 2 4 -9)
          actual (pt/intervals [0 3 5 9])]
      (is (= expected actual)))))

(deftest interval-vector-test
  (testing "Finding interval vector for pc set"
    (let [expected '([1 2] [2 2] [3 3] [4 1] [5 1] [6 1])
          actual (pt/interval-vector [3 11 0 9 2])]
      (is (= expected actual)))))

(deftest rotation-test
  (testing "Rotating pc sets"
    (let [expected '((:Eb :A :C :B :E :D)
                      (:Eb :Gb :F :Bb :Ab :A)
                      (:Eb :D :G :F :Gb :C)
                      (:Eb :Ab :Gb :G :Db :E)
                      (:Eb :Db :D :Ab :B :Bb)
                      (:Eb :E :Bb :Db :C :F))
          actual (->> (pt/rotate (first expected)) (map pt/named-set))]
      (is (= expected actual)))))
