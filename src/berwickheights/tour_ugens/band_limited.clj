(ns berwickheights.tour-ugens.band-limited
  (:use [overtone.core]))

; SinOsc
(demo 8 (let [fmod-inner (+ 10 (* 8 (sin-osc:kr 0.2)))
              fmod-outter (+ 800 (* 400 (sin-osc:kr fmod-inner)))
              sig (* 0.1 (sin-osc fmod-outter))]
          sig))
(demo 8 (let [fmod-outter (+ 800 (* 400 (sin-osc:kr 0.2)))
              sig (* 0.1 (sin-osc fmod-outter))]
          sig))
(demo 8 (let [sig (* 0.1 (sin-osc 800))]
          sig))

(demo 7 (let [freq (x-line:kr 10 1500 6)
              sig (sin-osc freq 0 0.1)]
          sig))

; Blip
(demo 2 (blip 200 100))
(demo 7 (blip (x-line:kr 20000 200 6) 100))
(demo 7 (blip (x-line:kr 100 15000 6) 100))
(demo 7 (blip 200 (x-line:kr 1 100 6)))

(demo 7 (blip (x-line:kr 100 15000 6) (x-line:kr 200 1 2)))

; Saw
(demo 7 (saw (x-line:kr 20000 200 6)))
(demo 7 (saw (x-line:kr 100 15000 6)))

; Pulse
(demo 7 (pulse (x-line:kr 20000 200 6) 0.3))
(demo 7 (pulse (x-line:kr 100 15000 6) 0.3))
(demo 9 (pulse 200 (x-line:kr 0.01 0.99 8)))
(demo 6 (rlpf (pulse [100 250] 0.5) (x-line:kr 8000 400 5) 0.04))

; Klang
(demo 5 (klang [[800 1000 1200] [0.3 0.3 0.3] [3.14 3.14 3.14]]))
(demo 2 (klang [(take 16 (repeatedly #(+ 400 (rand 1600))))]))
; TODO Use splay with klang (see doc for Klang: http://doc.sccode.org/Classes/Klang.html)

; BlitB3 -- not in Overtone
