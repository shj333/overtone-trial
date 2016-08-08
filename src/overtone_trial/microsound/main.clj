(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [overtone-trial.microsound.synths :as syn]
            [overtone-trial.microsound.aux :as aux]))


(do
  (ot/connect-external-server 4445)
  (let [env-bufs (micro/make-env-bufs)
        {:keys [trigger-busses triggers pan-busses pans]} (micro/make-triggers-pans)
        {:keys [reverb-bus producer-grp]} (aux/make-fx)]

    (def high-bells (syn/my-grain-sin [:tail producer-grp]
                                      :env-buf (:expodec env-bufs)
                                      :trigger-bus (:rand-sync trigger-busses)
                                      :pan-bus (:rand-sync pan-busses)
                                      :grain-dur 2.0
                                      :freq 2000
                                      :freq-dev-noise 1000
                                      :amp 0.1
                                      :out reverb-bus))))


(ot/kill high-bells)