(ns berwickheights.notes-metalevel.chapter17
  (:require [berwickheights.cac.play :as play])
  (:use overtone.core))

;
; Notes from the Metalevel, Chapter 17: Chaos
;

; See https://en.wikipedia.org/wiki/Logistic_map
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

; (volume 0.2)
; (use 'overtone.inst.sampled-piano)
; (def pno sampled-piano)
; (def nome (metronome 120))
;
; Converge to one note
; (play-logistic-map 40 2.8 60 72 nome pno [:sustain 0.1] 0.125)
;
; Converge to four notes
; (play-logistic-map 40 3.5 60 72 nome pno [:sustain 0.1] 0.125)
;
; Chaos
; Notes range from midi-low to midi-high on sampled piano
; (play-logistic-map 100 3.7 21 109 nome pno [:sustain 0.1] 0.125)
;
; Notes range from middle C to one octave up on sampled piano
; (play-logistic-map 100 3.7 60 72 nome pno [:sustain 0.1] 0.125)
;
