(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.instr-control :as instr]
            [overtone-trial.microsound.synths :as syn]
            [overtone-trial.microsound.aux :as aux]))


(defn play-high-bells
  [key grp env-bufs trigger-busses pan-busses reverb-bus]
  (instr/play-instr key syn/my-grain-sin
                    [:tail grp]
                    :env-buf (:expodec env-bufs)
                    :trigger-bus (:rand-sync trigger-busses)
                    :pan-bus (:rand-sync pan-busses)
                    :grain-dur 2.0
                    :freq 2000
                    :freq-dev-noise 1000
                    :amp 0.1
                    :out reverb-bus))

(defn play-low-rumble
  [key grp env-bufs trigger-busses pan-busses]
  (instr/play-instr key syn/my-grain-sin
                    [:tail grp]
                    :env-buf (:guass env-bufs)
                    :trigger-bus (:async trigger-busses)
                    :pan-bus (:async pan-busses)
                    :grain-dur 2.0
                    :freq 100
                    :freq-dev-noise 10
                    :amp 0.4))


(ot/connect-external-server 4445)

(do
  ; TODO Move all these data objs to individual namespaces and access via funcs so we don't have to pass them on the stack
  (let [env-bufs (micro/make-env-bufs)
        {:keys [trigger-busses triggers pan-busses pans]} (micro/make-triggers-pans)
        {:keys [reverb-bus producer-grp]} (aux/make-fx)
        curr-t (+ (ot/now) 2000)]
    (ot/at (+ curr-t 0) (play-high-bells :high-bells producer-grp env-bufs trigger-busses pan-busses reverb-bus))
    (ot/at (+ curr-t 3300) (play-low-rumble :low-rumble producer-grp env-bufs trigger-busses pan-busses))
    (ot/at (+ curr-t 11000) (instr/stop-instr :low-rumble))
    (ot/at (+ curr-t 15000) (instr/stop-instr :high-bells))))

(instr/stop-instr :high-bells)

