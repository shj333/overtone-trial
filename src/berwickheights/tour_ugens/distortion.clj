(ns berwickhheights.tour-ugens.distortion
  (:require [clojure.math.numeric-tower :as math])
  (:use overtone.live))

; Wave Shaping using Chebyshev Polynomials
(def buf (buffer 2048))
(def buf-id (buffer-id buf))
(apply snd "/b_gen" buf-id "cheby" 7 (take 24 (repeatedly #(math/expt (rand) 2))))
(demo 60 (shaper buf-id (* 0.3 (mouse-x:kr) (sin-osc (mouse-y:kr 10 500)))))


; Add ring modulation to wave shaping
(demo 60 (* 0.5 (sin-osc (* 1.1 (mouse-y:kr 10 500))) (shaper buf-id (* 0.3 (mouse-x:kr) (sin-osc (mouse-y:kr 10 500))))))

