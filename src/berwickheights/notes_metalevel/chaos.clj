(ns berwickheights.notes-metalevel.chaos
  (:require [berwickheights.cac.play :as play])
  (:use overtone.core))

(defn logistic-map
  "Returns lazy seq according to y(s, c) = c * y * (1 - y). From Chapter 17 of Notes from the Metalevel."
  ([chaos] (logistic-map (rand 1.0) chaos))
  ([prev-val chaos]
   (lazy-seq
     (let [new-val (* chaos prev-val (- 1 prev-val))]
       (cons new-val (logistic-map new-val chaos))))))

(defn play-logistic-map
  "Play n notes from logistic map sequence using the given instrument and metronome. The
  logistic map generates numbers using the given chaos constant. Notes are mapped from the
  logistic map sequence by scaling them betwen the given low and high."
  ([n chaos low high nome instr instr-vals] (play-logistic-map n chaos low high nome instr instr-vals 0.125))
  ([n chaos low high nome instr instr-vals beat-dur]
   (let [seq (map #(scale-range % 0 1.0 low high) (take n (logistic-map chaos)))]
     (play/play-sequence n seq nome instr instr-vals beat-dur))))

; (play-logistic-map 100 3.7 21 109 nome pno [:sustain 0.1] 0.125)
