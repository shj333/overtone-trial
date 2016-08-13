(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.instr-control :as instr]
            [berwickheights.cac.overtone.fx :as fx]
            [overtone-trial.microsound.synths :as syn]))


(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  (ot/sc-osc-debug-off)
  (micro/init)
  (micro/set-random-density-range 2 10)
  (fx/make-fx)
  (let [; TODO This could be loaded via YML file
        sound-defs {:high-bells {:synth  syn/my-grain-sin
                                 :params {:env-buf        :expodec
                                          :trigger-bus    :rand-sync
                                          :pan-bus        :rand-sync
                                          :grain-dur      2.0
                                          :freq           2000
                                          :freq-dev-noise 1000
                                          :amp            0.1
                                          :out            :reverb-bus}}
                    :low-rumble {:synth  syn/my-grain-sin
                                 :params {:env-buf        :guass
                                          :trigger-bus    :async
                                          :pan-bus        :async
                                          :grain-dur      2.0
                                          :freq           100
                                          :freq-dev-noise 10
                                          :amp            0.4}}
                    :glasses    {:synth  syn/my-grain-sin
                                 :params {:env-buf        :sinc5
                                          :trigger-bus    :rand-sync
                                          :pan-bus        :async
                                          :grain-dur      2.0
                                          :freq           1000
                                          :freq-dev-noise 100
                                          :amp            0.2
                                          :out            :reverb-bus}}}]
    (instr/define-sounds sound-defs)
    (instr/set-sect-start-time (+ (ot/now) 2000))
    (instr/play-sound 0 :high-bells :high-bells)
    (instr/play-sound 3300 :low-rumble :low-rumble)
    (instr/play-sound 7200 :glasses :glasses)
    (instr/stop-sound 11000 :low-rumble)
    (instr/set-amp 13000 :high-bells 0.05)
    (instr/set-amp 17000 :high-bells 0.02)
    (instr/stop-sound 20000 :high-bells)
    (instr/stop-sound 27000 :glasses)))

; (ot/kill (instr/instr :high-bells))
; (ot/kill (instr/instr :low-rumble))
