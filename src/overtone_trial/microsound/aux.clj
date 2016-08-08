(ns overtone-trial.microsound.aux
  (:require [overtone-trial.microsound.synths :as syn]
            [overtone.core :as ot]))

(defn- make-groups
  []
  (let [main-grp (ot/group "main group")
        producer-grp (ot/group "sound gen" :head main-grp)
        fx-grp (ot/group "fx" :after producer-grp)]
    {:main-grp main-grp :producer-grp producer-grp :fx-grp fx-grp}))

(defn make-fx
  []
  (let [reverb-bus (ot/audio-bus)
        {:keys [producer-grp fx-grp]} (make-groups)]
    (syn/reverb1 [:tail fx-grp] :in reverb-bus :room-size 243 :rev-time 1 :damping 0.1 :input-bw 0.34 :dry-level -3 :early-level -11 :tail-level -9)
    {:reverb-bus reverb-bus :producer-grp producer-grp}))
