(ns overtone-trial.microsound.examples
  (:require [overtone.core :as ot]
            [incanter core charts datasets]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.fx :as fx]
            [overtone-trial.microsound.synths :as syn]))


(let [env (:perc2 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(let [env (:sinc10 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))


(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  (micro/init)
  (fx/make-fx))



(def grain-inst (syn/my-grain-sin [:tail (fx/group :producer-grp)]
                                  :env-buf (micro/env-buf :perc1)
                                  :trigger-bus (micro/trigger-bus :sync)
                                  :pan-bus (micro/pan-bus :sync)
                                  :amp 0.2))

(ot/ctl grain-inst :trigger-bus (micro/trigger-bus :rand-sync))
(ot/ctl grain-inst :trigger-bus (micro/trigger-bus :sync))
(ot/ctl grain-inst :trigger-bus (micro/trigger-bus :async))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :sync))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :rand-sync))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :left))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :center-left))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :center))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :center-right))
(ot/ctl grain-inst :pan-bus (micro/pan-bus :right))
(ot/ctl grain-inst :env-buf (micro/env-buf :guass))
(ot/ctl grain-inst :env-buf (micro/env-buf :perc2))
(ot/ctl grain-inst :env-buf (micro/env-buf :expodec))
(ot/ctl grain-inst :env-buf (micro/env-buf :rexpodec))
(ot/ctl grain-inst :env-buf (micro/env-buf :sinc3))
(ot/ctl (micro/trigger :sync) :density 20)
(ot/ctl grain-inst :out (fx/bus :reverb-bus))
(ot/ctl grain-inst :out 0)
(ot/kill grain-inst)

(def high-bells (syn/my-grain-sin [:tail (fx/group :producer-grp)]
                                  :env-buf (micro/env-buf :expodec)
                                  :trigger-bus (micro/trigger-bus :rand-sync)
                                  :pan-bus (micro/pan-bus :rand-sync)
                                  :grain-dur 2.0
                                  :freq 2000
                                  :freq-dev-noise 1000
                                  :amp 0.1
                                  :out (fx/bus :reverb-bus)))
(def low-rumble (syn/my-grain-sin [:tail (fx/group :producer-grp)]
                                  :env-buf (micro/env-buf :guass)
                                  :trigger-bus (micro/trigger-bus :async)
                                  :pan-bus (micro/pan-bus :async)
                                  :grain-dur 2.0
                                  :freq 100
                                  :freq-dev-noise 10
                                  :amp 0.1))
(def glasses (syn/my-grain-sin [:tail (fx/group :producer-grp)]
                               :env-buf (micro/env-buf :sinc5)
                               :trigger-bus (micro/trigger-bus :rand-sync)
                               :pan-bus (micro/pan-bus :rand-sync)
                               :grain-dur 2.0
                               :freq 1000
                               :freq-dev-noise 100
                               :amp 0.02
                               :out (fx/bus :reverb-bus)))
(ot/ctl (micro/trigger :coin) :density 115 :prob 0.75)
(def shout (syn/my-grain-sin [:tail (fx/group :producer-grp)]
                             :env-buf (micro/env-bufs :sinc5)
                             :trigger-bus (micro/trigger-bus :coin)
                             :pan-bus (micro/pan-bus :coin)
                             :grain-dur 1.0
                             :freq 135
                             :freq-dev-noise 87
                             :mod-freq 50
                             :amp 0.05))
(def shout2 (syn/my-grain-sin [:tail (fx/group :producer-grp)]
                              :env-buf (micro/env-buf :sinc5)
                              :trigger-bus (micro/trigger-bus :coin)
                              :pan-bus (micro/pan-bus :coin)
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
