(ns berwickheights.dodge-jerse.chapter5
  (:use overtone.live)
  (:use [clojure.repl :only (source)]))


;
; Computer Music by Dodge & Jerse
; Chapter 5
;

; Section 5.1A
; Basic FM Synthesis
(definst basic-fm [car-freq 440 mod-freq 250 mod-index 2 amp 0.5 dur 5]
         (let [deviation (* mod-freq mod-index)
               modulator (+ car-freq (* deviation (sin-osc mod-freq)))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (sin-osc modulator))))
(basic-fm :mod-index 13)
(let [n (now)]
  (map #(let [this-idx (+ % 1)
              this-dur 1
              this-time (+ n (* 1000 % this-dur))]
         (at this-time (basic-fm :mod-index this-idx :dur this-dur)))
       (take 20 (range))))
