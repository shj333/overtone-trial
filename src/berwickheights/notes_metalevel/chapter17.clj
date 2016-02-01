(ns berwickheights.notes-metalevel.chapter17
  (:use [overtone.core]
        [overtone.inst.sampled-piano])
  (:require [berwickheights.cac.play :as play]))

;
; Notes from the Metalevel, Chapter 17: Chaos
;

; See https://en.wikipedia.org/wiki/Logistic_map
(defn logistic-map
  "Returns lazy seq according to new-val = r * old-val * (1 - old-val). From Chapter 17 of Notes from the Metalevel.
  R vals:
    1-3: converges to one note
    3-3.45: converges to two notes
    3.45-3.54: converges to four notes
    3.54-3.56: converges to 8, 16, 32 notes
    > 3.56: chaos"
  ([r] (logistic-map (rand 1.0) r))
  ([prev-val r]
   (lazy-seq
     (let [new-val (* r prev-val (- 1 prev-val))]
       (cons new-val (logistic-map new-val r))))))

(defn play-logistic-map
  [n r]
  (let [nome (metronome 120)
        pno sampled-piano
        seq (logistic-map r)]
    (play/play-sequence n seq 0 1.0 60 72 nome pno [:sustain 0.1] 0.125)))


(defn henon-map
  "Returns lazy seq of duples according to H(x, y) = ((y + 1) - ax2, bx), where a and b are constants (typically
  1.4 and 0.3). From Chapter 17 of Notes from the Metalevel."
  ([] (henon-map 1.4 0.3))
  ([a b] (henon-map 0 0 a b))
  ([x y a b]
   (lazy-seq
     (let [new-x (- (+ y 1) (* a x x))
           new-y (* b x)]
       (cons [new-x new-y] (henon-map new-x new-y a b))))))

(defn play-henon-map
  [n]
  (let [nome (metronome 120)
        pno sampled-piano
        seq (map first (henon-map))]
    (play/play-sequence n seq -5 5 21 108 nome pno [:sustain 0.1] 0.125)))
