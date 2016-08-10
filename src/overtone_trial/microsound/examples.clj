(ns overtone-trial.microsound.examples
  (:require [overtone.core :as ot]
            [incanter core charts datasets]
            [berwickheights.cac.overtone.microsound :as micro]
            [overtone-trial.microsound.synths :as syn]
            [overtone-trial.microsound.aux :as aux]))


(let [env (:perc2 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(let [env (:sinc10 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))


(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  (micro/init)
  (aux/make-fx))



(def grain-inst (syn/my-grain-sin [:tail aux/producer-grp]
                                  :env-buf (:perc1 micro/env-bufs)
                                  :trigger-bus (:sync micro/trigger-busses)
                                  :pan-bus (:sync micro/pan-busses)
                                  :amp 0.2))

(ot/ctl grain-inst :trigger-bus (:rand-sync micro/trigger-busses))
(ot/ctl grain-inst :trigger-bus (:sync micro/trigger-busses))
(ot/ctl grain-inst :trigger-bus (:async micro/trigger-busses))
(ot/ctl grain-inst :pan-bus (:sync micro/pan-busses))
(ot/ctl grain-inst :pan-bus (:rand-sync micro/pan-busses))
(ot/ctl grain-inst :pan-bus (:left micro/pan-busses))
(ot/ctl grain-inst :pan-bus (:center-left micro/pan-busses))
(ot/ctl grain-inst :pan-bus (:center micro/pan-busses))
(ot/ctl grain-inst :pan-bus (:center-right micro/pan-busses))
(ot/ctl grain-inst :pan-bus (:right micro/pan-busses))
(ot/ctl grain-inst :env-buf (:guass micro/env-bufs))
(ot/ctl grain-inst :env-buf (:perc2 micro/env-bufs))
(ot/ctl grain-inst :env-buf (:expodec micro/env-bufs))
(ot/ctl grain-inst :env-buf (:rexpodec micro/env-bufs))
(ot/ctl grain-inst :env-buf (:sinc3 micro/env-bufs))
(ot/ctl (:sync micro/triggers) :density 20)
(ot/ctl grain-inst :out aux/reverb-bus)
(ot/ctl grain-inst :out 0)
(ot/kill grain-inst)

(def high-bells (syn/my-grain-sin [:tail aux/producer-grp]
                                  :env-buf (:expodec micro/env-bufs)
                                  :trigger-bus (:rand-sync micro/trigger-busses)
                                  :pan-bus (:rand-sync micro/pan-busses)
                                  :grain-dur 2.0
                                  :freq 2000
                                  :freq-dev-noise 1000
                                  :amp 0.1
                                  :out aux/reverb-bus))
(def low-rumble (syn/my-grain-sin [:tail aux/producer-grp]
                                  :env-buf (:guass micro/env-bufs)
                                  :trigger-bus (:async micro/trigger-busses)
                                  :pan-bus (:async micro/pan-busses)
                                  :grain-dur 2.0
                                  :freq 100
                                  :freq-dev-noise 10
                                  :amp 0.1))
(def glasses (syn/my-grain-sin [:tail aux/producer-grp]
                               :env-buf (:sinc5 micro/env-bufs)
                               :trigger-bus (:rand-sync micro/trigger-busses)
                               :pan-bus (:rand-sync micro/pan-busses)
                               :grain-dur 2.0
                               :freq 1000
                               :freq-dev-noise 100
                               :amp 0.02
                               :out aux/reverb-bus))
(ot/ctl (:coin micro/triggers) :density 115 :prob 0.75)
(def shout (syn/my-grain-sin [:tail aux/producer-grp]
                             :env-buf (:sinc5 micro/env-bufs)
                             :trigger-bus (:coin micro/trigger-busses)
                             :pan-bus (:coin micro/pan-busses)
                             :grain-dur 1.0
                             :freq 135
                             :freq-dev-noise 87
                             :mod-freq 50
                             :amp 0.05))
(def shout2 (syn/my-grain-sin [:tail aux/producer-grp]
                              :env-buf (:sinc5 micro/env-bufs)
                              :trigger-bus (:coin micro/trigger-busses)
                              :pan-bus (:coin micro/pan-busses)
                              :grain-dur 1.0
                              :freq 305
                              :freq-dev-noise 87
                              :mod-freq 50
                              :amp 0.05))

(ot/kill high-bells)
(ot/kill low-rumble)
(ot/kill glasses)
(ot/kill shout)
(ot/kill shout2)
