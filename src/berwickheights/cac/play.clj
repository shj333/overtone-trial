(ns berwickheights.cac.play
  (:require [berwickheights.cac.pitch-transforms :as pt])
  (:require [overtone.core :as ot]))


(defn play-set
  "Plays the given set of pitch classes by mapping the set using the function map-pcs-to-pitches() with
  a given octave map. Uses the given instrument to play the pitches in the resulting set by converting
  each pitch with the given note-func.

  Example:
  (def pitch-oct-map [4 5 3 6 2 7 1 4 5 2 1 8])
  (play-set piano note [0 1 3] pitch-oct-map) => Plays pitch complex [C4 Db5 Eb6] using Overtone piano and note functions
  "
  [inst note-func set octave-map]
  (let [notes (map note-func (pt/map-pcs-to-pitches set octave-map))]
    (doseq [note notes] (inst note))))

(defn play-phrase
  "Plays the given phrase using the given instrument and metronome. Each item in the phrase is a tuple
  with beat offset and instrument data. A global beat offset for the entire phrase can also be given (default 0).

  Example:
  (let [phrase [[1 [:cf 2000 :amp 4.0]]
                [1.125 [:cf 2100 :amp 3.0]]
                [1.25 [:cf 2300 :amp 3.0]]
                [1.375 [:cf 1900 :amp 4.0]]
                [1.75 [:cf 2700 :amp 6.0]]
                [2.1 [:cf 1300 :amp 6.0]]]]
    (play-phrase pitch-perc phrase nome 0.6))) => Plays six events in a phrase, the entire phrase offset by 0.6 of a beat"
  ([instr phrase nome] (play-phrase instr phrase nome 0))
  ([instr phrase nome init-beat-offset]
   (let [beat (+ (nome) init-beat-offset)]
     (doseq [item phrase]
       (let [[beat-offset instr-data] item]
         (ot/at (nome (+ beat-offset beat)) (apply instr instr-data)))))))

(defn play-sequence
  "Play n notes from given sequence using the given instrument, instrument values and metronome.
  Each note has a duration according to given beat-dur (default 1/8 second)."
  ([n seq nome instr instr-vals] (play-sequence n seq nome instr instr-vals 0.125))
  ([n seq nome instr instr-vals beat-dur]
   (let [beat (nome)]
     (doseq [[idx val] (map-indexed vector seq)]
       (let [beat-offset (* idx beat-dur)]
         (ot/at (nome (+ beat-offset beat)) (apply instr val instr-vals)))))))

