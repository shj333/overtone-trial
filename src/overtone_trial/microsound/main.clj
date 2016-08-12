(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.instr-control :as instr]
            [overtone-trial.microsound.synths :as syn]
            [overtone-trial.microsound.aux :as aux]))


(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  (ot/sc-osc-debug-off)
  (micro/init)
  (micro/set-random-density-range 2 10)
  (aux/make-fx)
  (let [start-time (+ (ot/now) 2000)
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
                                          :amp 0.4]}
                    :glasses    {:synth  syn/my-grain-sin
                                 :params [[:tail aux/producer-grp]
                                          :env-buf (:sinc5 micro/env-bufs)
                                          :trigger-bus (:rand-sync micro/trigger-busses)
                                          :pan-bus (:async micro/pan-busses)
                                          :grain-dur 2.0
                                          :freq 1000
                                          :freq-dev-noise 100
                                          :amp 0.2
                                          :out aux/reverb-bus]}}]
    (instr/play-sound start-time 0 :high-bells (:high-bells sound-defs))
    (instr/play-sound start-time 3300 :low-rumble (:low-rumble sound-defs))
    (instr/play-sound start-time 7200 :glasses (:glasses sound-defs))
    (ot/apply-by (+ start-time 11000) #'instr/stop-instr [:low-rumble])
    (ot/apply-by (+ start-time 13000) #'instr/set-amp [:high-bells 0.05])
    (ot/apply-by (+ start-time 17000) #'instr/set-amp [:high-bells 0.02])
    (ot/apply-by (+ start-time 20000) #'instr/stop-instr [:high-bells])
    (ot/apply-by (+ start-time 27000) #'instr/stop-instr [:glasses])))

; (ot/kill (instr/instr :high-bells))
; (ot/kill (instr/instr :low-rumble))
