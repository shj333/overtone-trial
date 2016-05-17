(ns berwickheights.sc-book.chapter16-microsound
  (:use [overtone.core])
  (:require [berwickheights.cac.overtone.patterns :as pat]))

; Example 16.1 Short grain durations
; Gabor grain, Gaussian-shaped envelope
(defsynth gabor [out-bus [0 :ir] freq [440 :ir] sustain [1 :ir] pan [0.0 :ir] amp [0.1 :ir] width [0.25 :ir]]
          (let [env (lf-gauss:ar sustain width :loop 0 :action FREE)
                half-pi (* 0.5 (. Math PI))
                son (* env (f-sin-osc:ar freq half-pi))]
            (offset-out:ar out-bus (pan2:ar son pan amp))))
;(gabor)

; Approximation of above with a sine-shaped envelope
(defsynth gabor1 [out-bus [0 :ir] freq [440 :ir] sustain [1 :ir] pan [0.0 :ir] amp [0.1 :ir]]
          (let [snd (f-sin-osc:ar freq)
                env (env-gen:ar (env-sine sustain amp) :action FREE)]
            (offset-out:ar out-bus (pan2:ar (* snd env) amp))))
;(gabor1)



(def gabor-data (atom {:synth  gabor
                       :params {:out-bus 0
                                :freq    440
                                :sustain (cycle [0.001 0.1])
                                :pan     0.0
                                :amp     (cycle [0.1, 0.1])
                                :width   0.25
                                :dur     1000
                                }
                       }))
; Short grain, then 2x and 4x louder
;(pat/defpbind (now) gabor-data)
;(pat/defpat gabor-data :sustain (cycle [0.001 0.1]) :amp (cycle [0.2, 0.1]))
;(pat/defpat gabor-data :sustain (cycle [0.001 0.1]) :amp (cycle [0.4, 0.1]))
;(pat/defpat gabor-data :amp nil)


; Pitch to colored click
(def grain-data (atom {:synth  gabor
                       :params {:out-bus 0
                                :freq    1000
                                :sustain 0.02
                                :pan     0.0
                                :amp     0.2
                                :dur     500
                                }
                       }))
;(pat/defpbind (now) grain-data)
;(pat/defpat grain-data :sustain (repeatedly #(/ 10 (pat/pat-key grain-data :freq))))
;(pat/defpat grain-data :sustain (repeatedly #(/ 5 (pat/pat-key grain-data :freq))))
;(pat/defpat grain-data :sustain (repeatedly #(/ 3 (pat/pat-key grain-data :freq))))
;(pat/defpat grain-data :sustain (repeatedly #(/ 2 (pat/pat-key grain-data :freq))))
;(pat/defpat grain-data :sustain (repeatedly #(/ 1 (pat/pat-key grain-data :freq))))
;(pat/defpat grain-data :amp nil)


; Successively shorter, end
;(pat/defpat grain-data :sustain (map #(/ % (pat/pat-key grain-data :freq)) (range 10 0 -1)))


; Random drift of grain duration
;(pat/defpat grain-data :dur 100 :sustain (repeatedly #(/ (rand-int 10) (pat/pat-key grain-data :freq))))
;(pat/defpat grain-data :amp nil)
