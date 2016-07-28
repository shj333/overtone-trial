(ns berwickheights.dodge-jerse.chapter5
  (:use overtone.live))


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


; Section 5.1C Dynamic Spectra
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


; Section 5.1D Chowning Instruments using FM
; mod-freq-env and amp-env should change in dyn-spec-fm, but this gives the idea anyway
; Bell
(dyn-spec-fm :car-freq 200 :mod-freq 280 :index-of-mod 10 :dur 15)
; Wood Block
(dyn-spec-fm :car-freq 80 :mod-freq 55 :index-of-mod 25 :dur 0.2)
; Brass-like
(dyn-spec-fm :car-freq 440 :mod-freq 440 :index-of-mod 5 :dur 0.6)
; Muted Brass
(dyn-spec-fm :car-freq 440 :mod-freq 440 :index-of-mod 3 :dur 0.6)
; Clarinet
(dyn-spec-fm :car-freq 900 :mod-freq 600 :index-of-mod 3 :dur 1.5)


; Section 5.1E Two Carrier Oscillators
(definst two-car-fm [car1-freq 400 car2-freq 2000 mod-freq 50 index-of-mod1 4 index-of-mod2 2 amp 0.5 amp2 0.3 dur 5]
         (let [mod-freq-env 1   ; const modulation so we can hear format in this example
               mod-osc (* mod-freq (sin-osc mod-freq))
               deviation1 (* mod-freq index-of-mod1)
               mod1 (+ car1-freq (* deviation1 mod-osc))
               car1-osc (* amp (sin-osc mod1))
               mod2 (+ car2-freq (* (/ index-of-mod2 index-of-mod1) mod-osc))
               car2-osc (* amp amp2 (sin-osc mod2))
               amp-env (env-gen (env-perc :release dur) :action FREE)]
           (* amp-env (+ car1-osc car2-osc))))
; Should hear a constant formant around 2000 Hz; as amp2 is increased, hear stronger formant
(two-car-fm :car1-freq 400 :car2-freq 2000 :mod-freq 30 :index-of-mod1 4 :index-of-mod2 2 :amp2 0.8)


; Section 5.1G Complex Modulating Waves
(definst complex-mod-fm [car-freq 440 mod1-freq 500 mod2-freq 250 index-of-mod1 2 index-of-mod2 2 amp 0.5 dur 5]
         (let [mod1 (* mod1-freq index-of-mod1 (sin-osc mod1-freq))
               mod2 (* mod2-freq index-of-mod2 (sin-osc mod2-freq))
               amp-env (env-gen (env-perc :release dur) :action FREE)]
           (* amp amp-env (sin-osc (+ car-freq mod1 mod2)))))
(complex-mod-fm)

; Section 5.1H Instruments Simulation using Complex Modulating Waves
; This approximates the example given with only two modulating waves (the book uses three).
; See book for computation of mod1-freq, mod2-freq, index-of-mod1, index-of-mod2
(complex-mod-fm :car-freq 110 :mod1-freq 110 :mod2-freq 330 :index-of-mod1 4.7 :index-of-mod2 10.5)

; Like this idea as a percussive sound
(complex-mod-fm :car-freq 70 :mod1-freq 20 :mod2-freq 15 :index-of-mod1 3 :index-of-mod2 10 :dur 0.9 :amp 2.0)


; Section 5.2 Waveshaping
; See berwickheights.tour-ugens.distortion for a wave shaping example using
; "shaper" and chebyshev polynomials
