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
(defsynth reverb1
          [in 0 out 0 room-size 10 rev-time 3 damping 0.5 input-bw 0.5 spread 15 dry-level 1 early-level 0.7 tail-level 0.5 room-size 300]
          (let [sig (in:ar in)
                dry-level-amp (dbamp dry-level)
                early-level-amp (dbamp early-level)
                tail-level-amp (dbamp tail-level)]
            (out:ar out (g-verb:ar sig room-size rev-time damping input-bw spread dry-level-amp early-level-amp tail-level-amp room-size))))
