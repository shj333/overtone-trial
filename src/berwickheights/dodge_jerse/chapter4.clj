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
(definst ring-mod-dense-inst [car-freq 261 mod-freq 440 amp 0.1 dur 5]
         (let [mod-buf (buffer 2048)
               mod-id (buffer-id mod-buf)
               modulator (* amp (osc mod-id mod-freq))
               car-buf (buffer 2048)
               car-id (buffer-id car-buf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (apply snd "/b_gen" mod-id "sine1" 7 (map / (range 1.0 (inc 4))))
           (apply snd "/b_gen" car-id "sine1" 7 [0.5 0.75 0.25 0.15])
           (* modulator env (osc car-id car-freq))))
(ring-mod-dense-inst)
(ring-mod-dense-inst :car-freq 1315 :mod-freq 1113)


; Vibrato via Freq Modulation
(definst fm-vib-inst [freq 440 vib-width 10 vib-rate 7 amp 0.5 dur 5]
         (let [modulator (+ freq (* vib-width (sin-osc vib-rate)))
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (sin-osc modulator))))
(fm-vib-inst)


; Noise
(def noise-env (env-gen (env-perc :release 2) :action FREE))
(demo (* noise-env (white-noise)))
(demo (* noise-env (brown-noise)))
(demo (* noise-env (pink-noise)))
(demo (* noise-env (gray-noise)))
