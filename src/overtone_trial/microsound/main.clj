(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.instr-control :as instr]
            [overtone-trial.microsound.synths :as syn]
            [overtone-trial.microsound.aux :as aux]))





(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  ; (ot/sc-osc-debug-on)
  (micro/init)
  (aux/make-fx)
  (let [curr-t (+ (ot/now) 2000)
        ; TODO This could be loaded via YML file
        sound-defs {:high-bells {:synth  syn/my-grain-sin
                                 :params [[:tail aux/producer-grp]
                                          :env-buf (:expodec micro/env-bufs)
                                          :trigger-bus (:rand-sync micro/trigger-busses)
                                          :pan-bus (:rand-sync micro/pan-busses)
                                          :grain-dur 2.0
                                          :freq 2000
                                          :freq-dev-noise 1000
                                          :amp 0.1
                                          :out aux/reverb-bus]}
                    :low-rumble {:synth  syn/my-grain-sin
                                 :params [[:tail aux/producer-grp]
                                          :env-buf (:guass micro/env-bufs)
                                          :trigger-bus (:async micro/trigger-busses)
                                          :pan-bus (:async micro/pan-busses)
                                          :grain-dur 2.0
                                          :freq 100
                                          :freq-dev-noise 10
                                          :amp 0.4]}}]
    (instr/play-instr-at (+ curr-t 0) :high-bells (:synth (:high-bells sound-defs)) (:params (:high-bells sound-defs)))
    (instr/play-instr-at (+ curr-t 3300) :low-rumble (:synth (:low-rumble sound-defs)) (:params (:low-rumble sound-defs)))
    (ot/apply-by (+ curr-t 10000) #'instr/stop-instr [:low-rumble])
    (ot/apply-by (+ curr-t 15000) #'instr/stop-instr [:high-bells])))

; (ot/kill (instr/instr :high-bells))
; (ot/kill (instr/instr :low-rumble))
