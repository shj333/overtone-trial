(ns berwickhheights.tour_ugens.distortion
  (:require [clojure.math.numeric-tower :as math])
  (:use overtone.live))

; Chebyshev Polynomials
(def buf (buffer 2048))
(def buf-id (buffer-id buf))
(apply snd "/b_gen" buf-id "cheby" 7 (take 24 (repeatedly #(math/expt (rand) 2))))
(demo 60 (shaper buf-id (* 0.3 (mouse-x:kr) (sin-osc 440))))
