(ns berwickheights.cac.play
  (:require [berwickheights.cac.pitch-transforms :as pt])
  (:refer overtone.live :only [note])
  (:refer overtone.inst.piano :only [piano]))


(defn play-set [set octave-map]
  (let [notes (map note (pt/map-set-to-octaves set octave-map))]
    (doseq [note notes] (piano note))))
