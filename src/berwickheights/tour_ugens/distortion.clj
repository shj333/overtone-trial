(ns berwickheights.tour-ugens.distortion
  (:require [clojure.math.numeric-tower :as math])
  (:use overtone.core))

; Wave Shaping using Chebyshev Polynomials
(def buf (buffer 2048))
(def buf-id (buffer-id buf))
(apply snd "/b_gen" buf-id "cheby" 7 (take 24 (repeatedly #(math/expt (rand) 2))))
(demo 60 (shaper buf-id (* 0.3 (mouse-x:kr) (sin-osc (mouse-y:kr 10 500)))))


; Add ring modulation to wave shaping
(demo 60 (* 0.5 (sin-osc (* 1.1 (mouse-y:kr 10 500))) (shaper buf-id (* 0.3 (mouse-x:kr) (sin-osc (mouse-y:kr 10 500))))))

; Fundamental
(snd "/b_gen" buf-id "cheby" 7 1)
(demo 2 (shaper buf-id (sin-osc)))
; First partial (1 octave higher)
(snd "/b_gen" buf-id "cheby" 7 0 1)
(demo 2 (shaper buf-id (sin-osc)))
; Second partial (fifth)
(snd "/b_gen" buf-id "cheby" 7 0 0 1)
(demo 2 (shaper buf-id (sin-osc)))
; Third partial (2 oct higher)
(snd "/b_gen" buf-id "cheby" 7 0 0 0 1)
(demo 2 (shaper buf-id (sin-osc)))
; Fourth partial (third)
(snd "/b_gen" buf-id "cheby" 7 0 0 0 0 1)
(demo 2 (shaper buf-id (sin-osc)))

; Increasing energy up partials
(snd "/b_gen" buf-id "cheby" 7 0.05 0.1 0.2 0.3 0.4 0.5)
(demo 2 (shaper buf-id (sin-osc)))

; Every other partial
(snd "/b_gen" buf-id "cheby" 7 0.5 0 0.4 0 0.3 0 0.2)
(demo 2 (shaper buf-id (sin-osc)))
