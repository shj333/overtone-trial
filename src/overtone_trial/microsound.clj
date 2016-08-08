(ns overtone-trial.microsound
  (:use overtone.core)
  (:require [incanter core charts datasets]
            [berwickheights.cac.overtone.microsound :as micro]))

(let [env (:perc2 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(let [env (:sinc10 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))



(do
  (connect-external-server 4445)
  (defsynth my-grain-sin [out 0 env-buf -1 trigger-bus 0 grain-dur 0.1 freq 440 freq-dev-noise 400 amp 0.05 pan-bus 0]
            (let [trigger (in:kr trigger-bus 1)
                  pan (in:kr pan-bus 1)
                  freq-dev (* (white-noise:kr) freq-dev-noise)
                  this-freq (+ freq freq-dev)]
              (out:ar out (* amp (grain-sin:ar 2 trigger, grain-dur this-freq pan env-buf)))))

  (defsynth my-grain-fm [out 0 env-buf -1 trigger-bus 0 grain-dur 0.1 freq 440 freq-dev-noise 400 mod-freq 200 amp 0.05 pan-bus 0]
            (let [trigger (in:kr trigger-bus 1)
                  pan (in:kr pan-bus 1)
                  freq-dev (* (white-noise:kr) freq-dev-noise)
                  car-freq (+ freq freq-dev)
                  mod-depth (range-lin (lf-noise1:kr) 1 10)]
              (out:ar out (* amp (grain-fm:ar 2 trigger, grain-dur car-freq mod-freq mod-depth pan env-buf)))))

  (def env-bufs (micro/make-env-bufs))
  (def triggers-pans (micro/make-triggers-pans))
  (def triggers (:triggers triggers-pans))
  (def trigger-busses (:trigger-busses triggers-pans))
  (def pan-busses (:pan-busses triggers-pans))

  (defonce main-grp (group "main group"))
  (defonce producer-grp (group "sound gen" :head main-grp))
  (defonce fx-grp (group "fx" :after producer-grp))

  (def reverb-bus (audio-bus))
  (defsynth reverb1
            [in 0 out 0 room-size 10 rev-time 3 damping 0.5 input-bw 0.5 spread 15 dry-level 1 early-level 0.7 tail-level 0.5 room-size 300]
            (let [sig (in:ar in)
                  dry-level-amp (dbamp dry-level)
                  early-level-amp (dbamp early-level)
                  tail-level-amp (dbamp tail-level)]
              (out:ar out (g-verb:ar sig room-size rev-time damping input-bw spread dry-level-amp early-level-amp tail-level-amp room-size))))
  (def reverb-inst (reverb1 [:tail fx-grp] :in reverb-bus :room-size 243 :rev-time 1 :damping 0.1 :input-bw 0.34 :dry-level -3 :early-level -11 :tail-level -9)))



(def grain-inst (my-grain-sin [:tail producer-grp]
                              :env-buf (:perc1 env-bufs)
                              :trigger-bus (:sync trigger-busses)
                              :pan-bus (:sync pan-busses)
                              :amp 0.2))

(ctl grain-inst :trigger-bus (:rand-sync trigger-busses))
(ctl grain-inst :trigger-bus (:sync trigger-busses))
(ctl grain-inst :trigger-bus (:async trigger-busses))
(ctl grain-inst :pan-bus (:sync pan-busses))
(ctl grain-inst :pan-bus (:rand-sync pan-busses))
(ctl grain-inst :pan-bus (:left pan-busses))
(ctl grain-inst :pan-bus (:center-left pan-busses))
(ctl grain-inst :pan-bus (:center pan-busses))
(ctl grain-inst :pan-bus (:center-right pan-busses))
(ctl grain-inst :pan-bus (:right pan-busses))
(ctl grain-inst :env-buf (:guass env-bufs))
(ctl grain-inst :env-buf (:perc2 env-bufs))
(ctl grain-inst :env-buf (:expodec env-bufs))
(ctl grain-inst :env-buf (:rexpodec env-bufs))
(ctl grain-inst :env-buf (:sinc3 env-bufs))
(ctl (:sync triggers) :density 20)
(ctl grain-inst :out reverb-bus)
(ctl grain-inst :out 0)
(kill grain-inst)

(def high-bells (my-grain-sin [:tail producer-grp]
                              :env-buf (:expodec env-bufs)
                              :trigger-bus (:rand-sync trigger-busses)
                              :pan-bus (:rand-sync pan-busses)
                              :grain-dur 2.0
                              :freq 2000
                              :freq-dev-noise 1000
                              :amp 0.1
                              :out reverb-bus))
(def low-rumble (my-grain-sin [:tail producer-grp]
                              :env-buf (:guass env-bufs)
                              :trigger-bus (:async trigger-busses)
                              :pan-bus (:async pan-busses)
                              :grain-dur 2.0
                              :freq 100
                              :freq-dev-noise 10
                              :amp 0.1))
(def glasses (my-grain-sin [:tail producer-grp]
                           :env-buf (:sinc5 env-bufs)
                           :trigger-bus (:rand-sync trigger-busses)
                           :pan-bus (:rand-sync pan-busses)
                           :grain-dur 2.0
                           :freq 1000
                           :freq-dev-noise 100
                           :amp 0.02
                           :out reverb-bus))
(ctl (:coin triggers) :density 115 :prob 0.75)
(def shout (my-grain-sin [:tail producer-grp]
                         :env-buf (:sinc5 env-bufs)
                         :trigger-bus (:coin trigger-busses)
                         :pan-bus (:coin pan-busses)
                         :grain-dur 1.0
                         :freq 135
                         :freq-dev-noise 87
                         :mod-freq 50
                         :amp 0.05))
(def shout2 (my-grain-sin [:tail producer-grp]
                          :env-buf (:sinc5 env-bufs)
                          :trigger-bus (:coin trigger-busses)
                          :pan-bus (:coin pan-busses)
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



(let [env-bufs (micro/make-env-bufs)
      {:keys [trigger-busses triggers pan-busses pans]} (micro/make-triggers-pans)
      main-grp (group "main group")
      producer-grp (group "sound gen" :head main-grp)
      fx-grp (group "fx" :after producer-grp)
      reverb-bus (audio-bus)]

      (reverb1 [:tail fx-grp] :in reverb-bus :room-size 243 :rev-time 1 :damping 0.1 :input-bw 0.34 :dry-level -3 :early-level -11 :tail-level -9)
      (def high-bells (my-grain-sin [:tail producer-grp]
                                    :env-buf (:expodec env-bufs)
                                    :trigger-bus (:rand-sync trigger-busses)
                                    :pan-bus (:rand-sync pan-busses)
                                    :grain-dur 2.0
                                    :freq 2000
                                    :freq-dev-noise 1000
                                    :amp 0.1
                                    :out reverb-bus)))