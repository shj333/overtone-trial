(ns berwickheights.dodge-jerse.chapter6
  (:use overtone.live))

;
; Subtractive Synthesis
;

; 6.1 Sources

; Pulse Wave Form
; Modulate frequency
(demo 6 (* 0.2 (blip (x-line:kr 20000 20 6) 100)))

; Modulate number of harmonics
(demo 20 (* 0.2 (blip 200 (line:kr 1 100 20))))

; Pulse with only odd harmonics
; I don't think I have this right
(let [freq1 200
      freq2 (* 2 freq1)
      n1 60
      n2 50
      multiplier (/ n2 n1)]

      ;(demo 5 (* 0.2 (blip freq1 n1))))
      ;(demo 5 (* 0.2 (blip freq2 n2))))
      (demo 5 (* 0.2 (- (blip freq1 n1) (* multiplier (blip freq2 n2))))))

; 6.5 Filter Combinations
(definst filter-parallel [cf1 2000 cf2 3000 rq 0.4 amp 0.5 dur 5]
         (let [source (white-noise)
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (+ (bpf source cf1 rq) (bpf source cf2 rq)))))
(filter-parallel)
(filter-parallel :cf1 1000 :cf2 4000)
(filter-parallel :cf1 1000 :cf2 4000 :rq 0.8)
(filter-parallel :cf1 1000 :cf2 4000 :rq 0.1)

(definst filter-cascade [cf1 2000 cf2 3000 rq 0.4 amp 0.85 dur 5]
         (let [source (white-noise)
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (bpf (bpf source cf1 rq) cf2 rq))))
(filter-cascade)
(filter-cascade :cf1 1000 :cf2 4000)
(filter-cascade :cf1 1000 :cf2 4000 :rq 0.8)
(filter-cascade :cf1 1000 :cf2 4000 :rq 0.1)

; See Figure 6.14, band pass filter with wider, flatter passband, steeper rolloff
(filter-cascade :cf1 3800 :cf2 4000)

; 6.6 Adjustable Filters
(demo 10 (* 0.2 (brf (white-noise) (mouse-x:kr 100 4000) 0.8)))
(demo 10 (* 0.2 (brf (white-noise) 3000 (mouse-x:kr 0.1 0.7))))

; 6.8 Sub Synth Instruments that use noise sources
; Pitched percussion by choosing bandwidth of 5% of freq
(definst pitch-perc [cf 2000 amp 3.0 dur 1]
         (let [source (white-noise)
               bw (* 0.05 cf)
               rq (/ bw cf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (bpf source cf rq))))
(pitch-perc :cf 100 :amp 100.0)  ; Timpani?
(pitch-perc :cf 500 :amp 8.0)
(pitch-perc :cf 1000 :amp 8.0)
(pitch-perc :cf 2000 :amp 8.0)

; Figure 6.22
(definst gliss-bands [min-cf 261.6 ratio 2.828 min-pct-bw 0.05 range-bw 0.45 amp 0.2 dur 4]
         (let [source (white-noise)
               freq (/ 1 dur)
               cf-osc (+ min-cf (* ratio (sin-osc freq)))
               bw-osc (+ min-pct-bw (* range-bw (sin-osc freq)))
               cf (* min-cf cf-osc)
               bw (* cf bw-osc)
               rq (/ bw cf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (shared-out:kr 0 )
           (* amp env (bpf source cf rq))))
(gliss-bands)

