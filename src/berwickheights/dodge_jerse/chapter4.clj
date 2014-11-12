(ns
  ^{:author stuart}
  berwickheights.dodge_jerse.chapter4
  (:use overtone.live))

; Linear decay envelope is heard with a sharp drop off
(definst linear_dec [freq 120]
         (* (line 1 0 5 :action FREE)
            (lf-tri freq)))
(linear_dec 110)

; Exponential decay envelope is heard with smooth decay, more natural
(definst exp_dec [freq 120]
         (* (x-line 1 0.001 5 :action FREE)
            (lf-tri freq)))
(exp_dec 110)
