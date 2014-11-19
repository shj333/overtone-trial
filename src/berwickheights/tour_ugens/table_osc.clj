(ns berwickheights.tour-ugens.table-osc
  (:require [clojure.math.numeric-tower :as math])
  (:use overtone.live))

; Simple sine wave
(def buf (buffer 512))
(buffer-write! buf (create-buffer-data 512 #(Math/sin %) 0 TWO-PI))
(demo (* 0.1 (osc (buffer-id buf) 440)))

; Simple sine wave with only the fundamental
(def buf (buffer 2048))
(def buf-id (buffer-id buf))
(snd "/b_gen" buf-id "sine1" 7 1.0)
(demo 60 (* 0.1 (osc buf-id 100 0)))

; Change to more complex sound with decreasing strength of partials (first 6, 12, 24, 32)
(apply snd "/b_gen" buf-id "sine1" 7 (map / (range 1.0 (inc 6))))
(apply snd "/b_gen" buf-id "sine1" 7 (map / (range 1.0 (inc 12))))
(apply snd "/b_gen" buf-id "sine1" 7 (map / (range 1.0 (inc 24))))
(apply snd "/b_gen" buf-id "sine1" 7 (map / (range 1.0 (inc 32))))
(apply snd "/b_gen" buf-id "sine1" 7 (map #(math/expt % 2) (map / (range 1.0 (inc 32)))))

; COsc -- two oscillators, detuned
; Start sound, change buffer as above
(demo 60 (* 0.1 (c-osc buf-id 100 0.7)))

; Variable wavetable oscillator



