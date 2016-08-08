(ns overtone-trial.microsound.examples
  (:use overtone.core)
  (:require [incanter core charts datasets]
            [berwickheights.cac.overtone.microsound :as micro]
            [overtone-trial.microsound.synths]))


(let [env (:perc2 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(let [env (:sinc10 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))


(do
  (connect-external-server 4445)
  (def env-bufs (micro/make-env-bufs))
  (def triggers-pans (micro/make-triggers-pans))
  (def triggers (:triggers triggers-pans))
  (def trigger-busses (:trigger-busses triggers-pans))
  (def pan-busses (:pan-busses triggers-pans))

  (defonce main-grp (group "main group"))
  (defonce producer-grp (group "sound gen" :head main-grp))
  (defonce fx-grp (group "fx" :after producer-grp))

  (def reverb-bus (audio-bus))
  (reverb1 [:tail fx-grp] :in reverb-bus :room-size 243 :rev-time 1 :damping 0.1 :input-bw 0.34 :dry-level -3 :early-level -11 :tail-level -9))



(def grain-inst (my-grain-sin [:tail producer-grp]
                              :env-buf (:perc1 env-bufs)
                              :trigger-bus (:sync trigger-busses)
                              :pan-bus (:sync pan-busses)
                              :amp 0.2))

(ctl grain-inst :trigger-bus (:rand-sync trigger-busses))
(ctl grain-inst :trigger-bus (:sync trigger-busses))
(ctl grain-inst :trigger-bus (:async trigger-busses))
(ctl grain-inst :pan-bus (:sync pan-busses))
(ctl grain-inst :pan-bus (:rand-sync pan-busses))
(ctl grain-inst :pan-bus (:left pan-busses))
(ctl grain-inst :pan-bus (:center-left pan-busses))
(ctl grain-inst :pan-bus (:center pan-busses))
(ctl grain-inst :pan-bus (:center-right pan-busses))
(ctl grain-inst :pan-bus (:right pan-busses))
(ctl grain-inst :env-buf (:guass env-bufs))
(ctl grain-inst :env-buf (:perc2 env-bufs))
(ctl grain-inst :env-buf (:expodec env-bufs))
(ctl grain-inst :env-buf (:rexpodec env-bufs))
(ctl grain-inst :env-buf (:sinc3 env-bufs))
(ctl (:sync triggers) :density 20)
(ctl grain-inst :out reverb-bus)
(ctl grain-inst :out 0)
(kill grain-inst)

(def high-bells (my-grain-sin [:tail producer-grp]
                              :env-buf (:expodec env-bufs)
                              :trigger-bus (:rand-sync trigger-busses)
                              :pan-bus (:rand-sync pan-busses)
                              :grain-dur 2.0
                              :freq 2000
                              :freq-dev-noise 1000
                              :amp 0.1
                              :out reverb-bus))
(def low-rumble (my-grain-sin [:tail producer-grp]
                              :env-buf (:guass env-bufs)
                              :trigger-bus (:async trigger-busses)
                              :pan-bus (:async pan-busses)
                              :grain-dur 2.0
                              :freq 100
                              :freq-dev-noise 10
                              :amp 0.1))
(def glasses (my-grain-sin [:tail producer-grp]
                           :env-buf (:sinc5 env-bufs)
                           :trigger-bus (:rand-sync trigger-busses)
                           :pan-bus (:rand-sync pan-busses)
                           :grain-dur 2.0
                           :freq 1000
                           :freq-dev-noise 100
                           :amp 0.02
                           :out reverb-bus))
(ctl (:coin triggers) :density 115 :prob 0.75)
(def shout (my-grain-sin [:tail producer-grp]
                         :env-buf (:sinc5 env-bufs)
                         :trigger-bus (:coin trigger-busses)
                         :pan-bus (:coin pan-busses)
                         :grain-dur 1.0
                         :freq 135
                         :freq-dev-noise 87
                         :mod-freq 50
                         :amp 0.05))
(def shout2 (my-grain-sin [:tail producer-grp]
                          :env-buf (:sinc5 env-bufs)
                          :trigger-bus (:coin trigger-busses)
                          :pan-bus (:coin pan-busses)
                          :grain-dur 1.0
                          :freq 305
                          :freq-dev-noise 87
                          :mod-freq 50
                          :amp 0.05))

(kill high-bells)
(kill low-rumble)
(kill glasses)
(kill shout)
(kill shout2)
