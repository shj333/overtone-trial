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

; 6.8 Subtractive Synth Instruments that use noise sources
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

; Figure 6.22: Dynamic application of filter, cf goes from middle C to octave+tritone higher, bw goes from 5% to 50% of cf
(definst gliss-bands [start-midi 60 end-midi 78 min-bw-pct 0.05 bw-pct-range 0.45 amp 30 dur 4]
         (let [source (white-noise)
               min-cf (midicps start-midi)
               max-cf (midicps end-midi)
               cf (x-line:kr min-cf max-cf dur)
               bw (x-line:kr (* cf min-bw-pct) (* cf (+ min-bw-pct bw-pct-range)) dur)
               rq (/ bw cf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (bpf source cf rq))))
(gliss-bands)
(gliss-bands :dur 10)

; 6.9 Filtering periodic sources
; Figure 6.25: Amplitude env also drives the filter
(definst sin-filter [freq 440 filter-scale-factor 0.45 amp 1 dur 4]
         (let [source (sin-osc freq)
               env (env-gen (env-perc :release dur) :action FREE)
               cf 1
               bw (* 0.05 cf)
               rq 20.0]
           (* amp env (bpf source cf rq))))
(sin-filter :amp 20)
