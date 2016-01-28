(ns berwickheights.cac.play
  (:require [berwickheights.cac.pitch-transforms :as pt]))


(defn play-set
  "Plays the given set of pitch classes by mapping the set using the function map-set-to-octaves() with
  a given octave map. Uses the given instrument to play the pitches in the resulting set by converting
  each pitch with the given note-func.

  Example:
  (def pitch-oct-map [4 5 3 6 2 7 1 4 5 2 1 8])
  (play-set piano note [0 1 3] pitch-oct-map) => Plays pitch complex [C4 Db5 Eb6] using Overtone piano and note functions
  "
  [inst note-func set octave-map]
  (let [notes (map note-func (pt/map-set-to-octaves set octave-map))]
    (doseq [note notes] (inst note))))
