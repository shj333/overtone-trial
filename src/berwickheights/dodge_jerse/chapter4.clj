(ns berwickheights.dodge-jerse.chapter4
  (:use overtone.live))

;
; Computer Music by Dodge & Jerse
; Chapter 4
;

; Section 4.5
; Linear decay envelope is heard with a sharp drop off
(definst linear-dec [freq 120]
         (* (line 1 0 5 :action FREE)
            (lf-tri freq)))
(linear-dec 110)

; Exponential decay envelope is heard with smooth decay, more natural
(definst exp-dec [freq 120]
         (* (x-line 1 0.001 5 :action FREE)
            (lf-tri freq)))
(exp-dec 110)


; Section 4.8
; Amplitude Modulation
(definst am-inst [car-freq 440 mod-freq 3 mod-index 0.5 amp 0.5 dur 5]
         (let [modulator (+ amp (* mod-index amp (sin-osc mod-freq)))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* modulator env (sin-osc car-freq))))
(am-inst :mod-index 0 :mod-freq 100)    ; No modulation, simple sine tone
(am-inst :mod-index 1 :mod-freq 3)      ; Vibrato
(am-inst :mod-index 1 :mod-freq 7)      ; Fast vibrato
(am-inst :mod-index 0.5 :mod-freq 100)  ; Side bands

(definst am-funky [car-freq 440 mod-freq 3 mod-index 0.5 amp 0.5 dur 5]
         (let [modulator (+ amp (* mod-index amp (lf-tri mod-freq)))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* modulator env (sin-osc car-freq))))
(am-funky :mod-index 0.15 :mod-freq 50)


; Ring Modulation
(definst ring-mod-inst [car-freq 261 mod-freq 440 amp 0.5 dur 5]
         (let [modulator (* amp (sin-osc mod-freq))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* modulator env (sin-osc car-freq))))
(ring-mod-inst)

; Ring Modulation with dense spectra
(definst ring-mod-dense [car-freq 261 mod-freq 440 amp 0.1 dur 5]
         (let [mod-buf (buffer 2048)
               mod-id (buffer-id mod-buf)
               modulator (* amp (osc mod-id mod-freq))
               car-buf (buffer 2048)
               car-id (buffer-id car-buf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (apply snd "/b_gen" mod-id "sine1" 7 (map / (range 1.0 (inc 4))))
           (snd "/b_gen" car-id "sine1" 7 0.5 0.75 0.25 0.15)
           (* modulator env (osc car-id car-freq))))
(ring-mod-dense)
(ring-mod-dense :car-freq 1315 :mod-freq 1113)


; Vibrato via Freq Modulation
(definst fm-vib [freq 440 vib-width 10 vib-rate 7 amp 0.5 dur 5]
         (let [modulator (+ freq (* vib-width (sin-osc vib-rate)))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (sin-osc modulator))))
(fm-vib)


; Section 4.9
; Noise
; Equivalents of RAND generator
(def noise-env (env-gen (env-perc :release 2) :action FREE))
(demo (* noise-env (white-noise)))
(demo (* noise-env (brown-noise)))
(demo (* noise-env (pink-noise)))
(demo (* noise-env (gray-noise)))

; Equivalent of RANDH generator (sample and hold)
(demo (* noise-env (lf-noise0 1760)))

; Equivalent of RANDI generator (linear interpolation between samples)
(demo (* noise-env (lf-noise1 1760)))

; Another equivalent of RANDI generator (quadratic interpolation between samples)
(demo (* noise-env (lf-noise2 1760)))



; Section 4.11
; Noise and Ring Modulation
(definst noise-ring [freq 440 rand-freq 10 vib-rate 7 amp 0.5 dur 5]
         (let [env (env-gen (env-perc :release dur) :action FREE)
               ; NOTE: If lf-noise0 is used, noise causes more choppy sound, lf-noise1 less so, lf-noise2 smooth
               noise (* env (lf-noise2 rand-freq))]
           (* amp noise (sin-osc freq))))
(noise-ring :rand-freq 4)
(noise-ring :rand-freq 20)
(noise-ring :rand-freq 44)
(noise-ring :rand-freq 100)
(noise-ring :rand-freq 200)

; Use glissando for noise freq, sounds seems to expand outward as more noise is added
(definst gliss-noise-ring [freq 440 rand-freq 10 vib-rate 7 amp 1.0 dur 10]
         (let [env (env-gen (env-perc :release dur) :action FREE)
               noise (* env (lf-noise2 (line rand-freq (* 10 rand-freq) dur)))]
           (* amp noise (sin-osc freq))))
(gliss-noise-ring :rand-freq 44)
(gliss-noise-ring :rand-freq 100)
(gliss-noise-ring :rand-freq 200)
(gliss-noise-ring :rand-freq 400)


; Risset drum instrument
; Create a wave with partials at 10, 16, 22 and 23 with relative amps of 1, 1.5, 2 and 1.5
(def inharm-buf (buffer 2048))
(def inharm-buf-id (buffer-id inharm-buf))
(snd "/b_gen" buf-id "sine2" 7 10 0.5 16 0.75 22 1.0 23 0.75)
; (demo 5 (* 0.5 (osc buf-id 100 0)))

; Instrument as designed by Risset using above inharmonic wave stored in buffer
(definst risset-drum [freq 200 amp 3 dur 0.5]
         ; inharm-env and randi-env have sharper decay than fund-env
         (let [fund-env (env-gen (env-perc :release dur :level (/ amp 2.5) :curve -4) :action FREE)
               inharm-env (env-gen (env-perc :release dur :level (/ amp 6) :curve -8) :action FREE)
               randi-env (env-gen (env-perc :release dur :level (/ amp 2) :curve -8) :action FREE)
               randi (* randi-env (lf-noise1 400))
               noise (* randi (sin-osc 500))
               inharm (* inharm-env (osc inharm-buf-id (/ freq 10)))
               fund (* fund-env (sin-osc freq))]
           (+ noise inharm fund)))
; snare
(risset-drum)
; bass drum
(risset-drum :freq 50)
; bongo
(risset-drum :freq 100)
; definite pitch due to longer duration
(risset-drum :dur 3)

; Risset bell
(definst risset-bell [freq 200 amp 1 dur 20]
         (let [data [{:amp 1    :dur 1     :freq-m 0.56 }
                     {:amp 0.67 :dur 0.9   :freq-m 0.56 :freq-a 1 }
                     {:amp 1    :dur 0.65  :freq-m 0.92 }
                     {:amp 1.8  :dur 0.55  :freq-m 0.92 :freq-a 1.7}
                     {:amp 2.67 :dur 0.325 :freq-m 1.19 }
                     {:amp 1.67 :dur 0.35  :freq-m 1.7  }
                     {:amp 1.46 :dur 0.25  :freq-m 2    }
                     {:amp 1.33 :dur 0.2   :freq-m 2.74 }
                     {:amp 1.33 :dur 0.15  :freq-m 3    }
                     {:amp 1    :dur 0.1   :freq-m 3.76 }
                     {:amp 1.33 :dur 0.75  :freq-m 4.07 }
                    ]]
           (reduce + (map #(let [this-freq (+ (* freq (% :freq-m) (% :freq-a 0)))
                                  this-amp  (* amp (% :amp))
                                  this-dur  (* dur (% :dur))
                                  env (env-gen (env-lin :attack 0.0001 :release this-dur :level this-amp :curve :exp) :action FREE)]
                              (* env (sin-osc this-freq))) data))))
(risset-bell)


