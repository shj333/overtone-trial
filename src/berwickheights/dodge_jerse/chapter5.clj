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

; Figure 5.6, inharmonic spectrum at 5 different frequencies (ratio of fc:fm ~= 1:sqrt(2))
(let [n (now)]
  (map #(let [this-car-freq (* % 100)
              this-mod-freq (* 1.4 this-car-freq)
              this-dur 3
              this-time (+ n 1000 (* 1000 (- % 1) this-dur))]
         (at this-time (basic-fm :car-freq this-car-freq :mod-freq this-mod-freq :index-of-mod 1 :dur this-dur :amp 1.0)))
       (take 5 (iterate inc 1))))

; Figure 5.7, simple instrument with dynamic spectra
; Note: Try this using different envs for the mod-freq-env (env-lin, env-sine, env-triangle, etc)
(definst dyn-spec-fm [car-freq 440 mod-freq 250 index-of-mod 2 amp 0.5 dur 5]
         (let [mod-freq-env (env-gen (env-perc :release dur) :action FREE)
               deviation (* mod-freq index-of-mod mod-freq-env)
               modulator (+ car-freq (* deviation (sin-osc mod-freq)))
               amp-env (env-gen (env-perc :release dur) :action FREE)]
           (* amp amp-env (sin-osc modulator))))

; Examples of above, note how spectra changes over time in dyn-spec-fm, but stays same in basic-rm
(basic-fm :car-freq 200 :mod-freq 280 :index-of-mod 5)
(dyn-spec-fm :car-freq 200 :mod-freq 280 :index-of-mod 5)
