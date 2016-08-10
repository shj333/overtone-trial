(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.instr-control :as instr]
            [overtone-trial.microsound.synths :as syn]
            [overtone-trial.microsound.aux :as aux]))


(defn play-high-bells
  [key]
  (instr/play-instr key syn/my-grain-sin
                    [:tail aux/producer-grp]
                    :env-buf (:expodec micro/env-bufs)
                    :trigger-bus (:rand-sync micro/trigger-busses)
                    :pan-bus (:rand-sync micro/pan-busses)
                    :grain-dur 2.0
                    :freq 2000
                    :freq-dev-noise 1000
                    :amp 0.1
                    :out aux/reverb-bus))

(defn play-low-rumble
  [key]
  (instr/play-instr key syn/my-grain-sin
                    [:tail aux/producer-grp]
                    :env-buf (:guass micro/env-bufs)
                    :trigger-bus (:async micro/trigger-busses)
                    :pan-bus (:async micro/pan-busses)
                    :grain-dur 2.0
                    :freq 100
                    :freq-dev-noise 10
                    :amp 0.4))



(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  (micro/init)
  (aux/make-fx)
  (let [curr-t (+ (ot/now) 2000)]
    (ot/at (+ curr-t 0) (play-high-bells :high-bells))
    (ot/at (+ curr-t 3300) (play-low-rumble :low-rumble))
    (ot/at (+ curr-t 11000) (instr/stop-instr :low-rumble))
    (ot/at (+ curr-t 15000) (instr/stop-instr :high-bells))))

(instr/stop-instr :high-bells)

