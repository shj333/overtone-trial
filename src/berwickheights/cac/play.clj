(ns berwickheights.cac.play
  (:require [berwickheights.cac.pitch-transforms :as pt])
  (:require [overtone.core :as ot])
  (:require [overtone.inst.piano :as pno]))


(defn play-set
  "Plays the given set of pitch classes by mapping the set using the function map-set-to-octaves() with
  a given octave map. Uses the Overtone piano function to play the pitches in the resulting set.

  Example:
  (def pitch-oct-map [4 5 3 6 2 7 1 4 5 2 1 8])
  (play-set [0 1 3] pitch-oct-map)
  "
  [set octave-map]
  (let [notes (map ot/note (pt/map-set-to-octaves set octave-map))]
    (doseq [note notes] (pno/piano note))))
