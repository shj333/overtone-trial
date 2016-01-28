(ns berwickheights.cac.examples.boulez-on-music-today
  (:require [berwickheights.cac.pitch-transforms :as pt]))

; From Boulez On Music Today, page 40
(def example-multi-src-set [:Ab :C :A :B])
(def example-multi-parts [[:D :F :Eb] [:Bb :Db] example-multi-src-set [:E :G] [:Gb]])
(defn boulez-example [set transpose-level]
  (-> (pt/boulez-multi set example-multi-src-set)
      (pt/transform transpose-level pt/transpose)
      pt/named-set))

(map boulez-example example-multi-parts (repeat 6))
(map boulez-example example-multi-parts (repeat 9))
(map boulez-example example-multi-parts (repeat 7))
