(ns overtone-trial.microsound.main
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.instr-control :as instr]
            [berwickheights.cac.overtone.sect-control :as sect]
            [berwickheights.cac.overtone.fx :as fx]
            [overtone-trial.microsound.synths :as syn]))

; TODO This could be loaded via YML file
(def sound-defs {:high-bells {:synth  syn/my-grain-sin
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
                                       :out            :reverb-bus}}})


(def sections-data [{:name   "Section 1"
                     :length 30
                     :events [[:play 0 :high-bells]
                              [:play 3300 :low-rumble]
                              [:play 7200 :glasses]
                              [:stop 11000 :low-rumble]
                              [:amp 13000 :high-bells 0.05]
                              [:amp 17000 :high-bells 0.02]
                              [:stop 20000 :high-bells]
                              [:stop 27000 :glasses]]}
                    {:name   "Section 2"
                     :length 10
                     :events [[:play 0 :high-bells]
                              [:play 3300 :low-rumble]
                              [:stop 5000 :low-rumble]
                              [:stop 7000 :high-bells]]}
                    {:name   "Done"
                     :length 0
                     :events []}])


(do
  (if (ot/server-disconnected?) (ot/connect-external-server 4445))
  (ot/sc-osc-debug-off)
  (micro/init)
  (micro/set-random-density-range 2 10)
  (fx/make-fx)
  (instr/define-sounds sound-defs)
  (sect/play-sections sections-data))

; (ot/kill (instr/instr :high-bells))
; (ot/kill (instr/instr :low-rumble))
