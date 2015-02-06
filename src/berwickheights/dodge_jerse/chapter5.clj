(ns berwickheights.dodge-jerse.chapter5
  (:use overtone.live)
  (:use [clojure.repl :only (source)]))


;
; Computer Music by Dodge & Jerse
; Chapter 5
;

; Section 5.1A
; Basic FM Synthesis (Figure 5.1)
;
; Index of modulation is defined as I = Fm / D   (where D is deviation, see Figure 5.1)
; So, Deviation can be defined as D = Fm * I, which allows the user to specify I instead of D
;
(definst basic-fm [car-freq 440 mod-freq 250 index-of-mod 2 amp 0.5 dur 5]
         (let [deviation (* mod-freq index-of-mod)
               modulator (+ car-freq (* deviation (sin-osc mod-freq)))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (sin-osc modulator))))

; Play sound for three seconds with index of modulation set to 5, 10, 15, 20 and 25
(let [n (now)]
  (map #(let [this-idx-of-mod (* % 5)
              this-dur 3
              this-time (+ n 1000 (* 1000 (- % 1) this-dur))]
         (at this-time (basic-fm :index-of-mod this-idx-of-mod :dur this-dur)))
       (take 5 (iterate inc 1))))

; Figure 5.6, inharmonic spectrum
(basic-fm :car-freq 100 :mod-freq (* 1.4 100) :index-of-mod 1 :dur 3)

