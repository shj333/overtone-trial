(ns overtone-trial.microsound
  (:use overtone.live)
  (:require [incanter core charts datasets]
            [berwickheights.cac.overtone.microsound :as micro]))

(let [env (:perc2 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(let [env (:sinc10 micro/env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))

(def env-bufs (micro/make-env-bufs))

(definst my-grain-sin [out 0 env-buf -1 trigger-bus 0 grain-dur 0.1 freq 440 freq-dev-noise 400 amp 0.05]
         (let [[trigger pan] (in:kr trigger-bus 2)
               freq-dev (* (white-noise:kr) freq-dev-noise)
               this-freq (+ freq freq-dev)]
           (out:ar out (* amp (grain-sin:ar 2 trigger, grain-dur this-freq pan env-buf)))))

(def trigger-defs {:sync  (definst sync-trigger [out 0 density 1] (out:kr out [(impulse:kr density) (lf-noise0:kr density)]))
                   :async (definst async-trigger [out 0 density 1] (out:kr out [(dust:kr density) (lf-noise0:kr density)]))
                   :coin  (definst coin-trigger [out 0 density 1 prob 0.5] (out:kr out [(coin-gate:kr prob (impulse:kr density)) (lf-noise0:kr density)]))})

(def trigger-busses (into {} (for [key [:sync :async :coin]] [key (control-bus 2)])))
(def triggers (into {} (for [[key bus] trigger-busses] [key ((key trigger-defs) :out bus)])))


(def grain-inst (my-grain-sin :env-buf (buffer-id (:perc1 env-bufs))
                              :trigger-bus (:sync trigger-busses)
                              :amp 0.2))
(ctl grain-inst :trigger-bus (:sync trigger-busses))
(ctl grain-inst :env-buf (buffer-id (:sine env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:perc2 env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:expodec env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:rexpodec env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:sinc3 env-bufs)))
(ctl (:sync triggers) :density 20)

(stop)
