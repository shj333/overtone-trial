(ns overtone-trial.microsound.examples
  (:use overtone.core)
  (:require [incanter core charts datasets]
            [berwickheights.cac.overtone.microsound :as micro]
            [overtone-trial.microsound.synths :as syn]))


(let [env (:perc2 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(let [env (:sinc10 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))


(do
  (connect-external-server 4445)
  (micro/init))

(do
  (def main-grp (group "main group"))
  (def producer-grp (group "sound gen" :head main-grp))
  (def fx-grp (group "fx" :after producer-grp))

  (def reverb-bus (audio-bus))
  (syn/reverb1 [:tail fx-grp] :in reverb-bus :room-size 243 :rev-time 1 :damping 0.1 :input-bw 0.34 :dry-level -3 :early-level -11 :tail-level -9))



(def grain-inst (syn/my-grain-sin [:tail producer-grp]
                                  :env-buf (:perc1 micro/env-bufs)
                                  :trigger-bus (:sync micro/trigger-busses)
                                  :pan-bus (:sync micro/pan-busses)
                                  :amp 0.2))

(ctl grain-inst :trigger-bus (:rand-sync micro/trigger-busses))
(ctl grain-inst :trigger-bus (:sync micro/trigger-busses))
(ctl grain-inst :trigger-bus (:async micro/trigger-busses))
(ctl grain-inst :pan-bus (:sync micro/pan-busses))
(ctl grain-inst :pan-bus (:rand-sync micro/pan-busses))
(ctl grain-inst :pan-bus (:left micro/pan-busses))
(ctl grain-inst :pan-bus (:center-left micro/pan-busses))
(ctl grain-inst :pan-bus (:center micro/pan-busses))
(ctl grain-inst :pan-bus (:center-right micro/pan-busses))
(ctl grain-inst :pan-bus (:right micro/pan-busses))
(ctl grain-inst :env-buf (:guass micro/env-bufs))
(ctl grain-inst :env-buf (:perc2 micro/env-bufs))
(ctl grain-inst :env-buf (:expodec micro/env-bufs))
(ctl grain-inst :env-buf (:rexpodec micro/env-bufs))
(ctl grain-inst :env-buf (:sinc3 micro/env-bufs))
(ctl (:sync micro/triggers) :density 20)
(ctl grain-inst :out reverb-bus)
(ctl grain-inst :out 0)
(kill grain-inst)

(def high-bells (syn/my-grain-sin [:tail producer-grp]
                                  :env-buf (:expodec micro/env-bufs)
                                  :trigger-bus (:rand-sync micro/trigger-busses)
                                  :pan-bus (:rand-sync micro/pan-busses)
                                  :grain-dur 2.0
                                  :freq 2000
                                  :freq-dev-noise 1000
                                  :amp 0.1
                                  :out reverb-bus))
(def low-rumble (syn/my-grain-sin [:tail producer-grp]
                                  :env-buf (:guass micro/env-bufs)
                                  :trigger-bus (:async micro/trigger-busses)
                                  :pan-bus (:async micro/pan-busses)
                                  :grain-dur 2.0
                                  :freq 100
                                  :freq-dev-noise 10
                                  :amp 0.1))
(def glasses (syn/my-grain-sin [:tail producer-grp]
                               :env-buf (:sinc5 micro/env-bufs)
                               :trigger-bus (:rand-sync micro/trigger-busses)
                               :pan-bus (:rand-sync micro/pan-busses)
                               :grain-dur 2.0
                               :freq 1000
                               :freq-dev-noise 100
                               :amp 0.02
                               :out reverb-bus))
(ctl (:coin micro/triggers) :density 115 :prob 0.75)
(def shout (syn/my-grain-sin [:tail producer-grp]
                             :env-buf (:sinc5 micro/env-bufs)
                             :trigger-bus (:coin micro/trigger-busses)
                             :pan-bus (:coin micro/pan-busses)
                             :grain-dur 1.0
                             :freq 135
                             :freq-dev-noise 87
                             :mod-freq 50
                             :amp 0.05))
(def shout2 (syn/my-grain-sin [:tail producer-grp]
                              :env-buf (:sinc5 micro/env-bufs)
                              :trigger-bus (:coin micro/trigger-busses)
                              :pan-bus (:coin micro/pan-busses)
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
