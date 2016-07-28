(ns berwickheights.notes-metalevel.chapter17
  (:use [overtone.core]
        [overtone.inst.sampled-piano])
  (:require [berwickheights.cac.chaos :as chaos])
  (:require [berwickheights.cac.play :as play]))

;
; Notes from the Metalevel, Chapter 17: Chaos
;

(defn play-logistic-map
  [n r]
  (let [nome (metronome 120)
        pno sampled-piano
        seq (chaos/logistic-map r)]
    (play/play-sequence n seq 0 1.0 60 72 nome pno [:sustain 0.1] 0.125)))


(defn play-henon-map
  [n]
  (let [nome (metronome 120)
        pno sampled-piano
        seq (map first (chaos/henon-map))]
    (play/play-sequence n seq -5 5 21 108 nome pno [:sustain 0.1] 0.125)))
