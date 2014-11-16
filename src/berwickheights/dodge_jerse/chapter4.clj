(ns
  ^{:author stuart}
  berwickheights.dodge_jerse.chapter4
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

(demo 2 (apply * 0.4 (map #(sin-osc %) (take 5 (repeatedly #(rand 1000))))))

