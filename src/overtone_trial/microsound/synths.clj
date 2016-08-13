(ns overtone-trial.microsound.synths
  (:use overtone.core))


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
