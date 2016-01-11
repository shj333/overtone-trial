(ns berwickheights.dodge-jerse.chapter7
  (:use overtone.live))

(def a (buffer 2048))
(def b (buffer 2048))

; From Overtone Examples
(demo 10
      (let [input  (sound-in) ; mic
            src    (white-noise) ; synth - try replacing this with other sound sources
            formed (pv-mul (fft a input) (fft b src))
            audio  (ifft formed)]
        (pan2 (* 0.7 audio))))


; See http://doc.sccode.org/Guides/FFT-Overview.html
(demo 10
      (let [input  (white-noise)
            formed (pv-rand-comb (fft a input) 0.95 (impulse:kr 0.4))
            audio  (ifft formed)]
        (pan2 (* 0.7 audio))))

