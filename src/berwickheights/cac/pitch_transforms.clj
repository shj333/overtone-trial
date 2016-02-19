(ns berwickheights.cac.pitch-transforms
  (:require [clojure.math.combinatorics :as combo]))

; Map pitch classes to pitch name keywords (and vice-versa via indexOf)
(def pitch-names [:C :Db :D :Eb :E :F :Gb :G :Ab :A :Bb :B])

(defn numeric-set
  "Converts given set to set of pitch class numbers. If the set is already using pitch class numbers, then
  returns original set. Otherwise, set is converted to a set of numbers from 0-11 representing pitch
  classes."
  [set]
  (if (keyword? (first set))
    (map #(.indexOf pitch-names %) set)
    set))

(defn named-set
  "Converts given set to set of pitch name keywords. If the set is already using pitch name, then returns
  original set. Otherwise, set is converted to a set of keywords representing the pitches."
  [set]
  (if (keyword? (first set)) set (map pitch-names set)))


(defn transpose
  "Transpose the given pitch class by the given interval. Returns the transposed pitch class."
  [pc interval]
  (mod (+ pc interval) 12))

(defn invert
  "Inverts the given pitch class by the given interval. Returns the inverted pitch class."
  [pc interval]
  (mod (+ (- 12 pc) interval) 12))

(defn transform
  "Transforms the given pitch class set by the given interval using either transposition or inversion
  depending on the given function (transpose or invert). Returns the transformed pitch class set.

  Example:
  (transform [0 1 3] 6 transpose) => (6 7 9)"
  [pc-set interval f]
  (map f pc-set (repeat interval)))

(defn transform-many
  "Transforms the given pitch class set by the given intervals using either transposition or inversion
  depending on the given function (transpose or invert). Returns a sequence of transformed pitch class sets.

  Example:
  (transform-many [0 1 3] [2 4 6] transpose) => ((2 3 5) (4 5 7) (6 7 9))"
  [pc-set intervals f]
  (map transform (repeat pc-set) intervals (repeat f)))

(defn retro
  "Convenience function that retrogrades the given pitch class set after transposing it using the given
  interval. Returns the retrograded pitch class set."
  [pc-set interval]
  (reverse (transform pc-set interval transpose)))

(defn retro-many
  "Convenience function that retrogrades the given pitch class set after transposing it using the given
  intervals. Returns a sequence of the retrograded pitch class sets."
  [pc-set intervals]
  (map retro (repeat pc-set) intervals))

(defn boulez-multi
  "Boulez pitch multiplication using the two given sets. See \"Boulez on Music Today\"
  and http://www.artsjournal.com/postclassic/2010/06/how_to_care_how_it_was_made.html"
  [pc-set1 pc-set2]
  (let [set1-num (numeric-set pc-set1)
        set2-num (numeric-set pc-set2)
        first-pitch (first set2-num)
        set2-zeroed (map - set2-num (repeat first-pitch))]
    (-> (transform-many set1-num set2-zeroed transpose)
        flatten)))

(defn gen-pitch
  "Generates a pitch keyword from the given pitch class and pitch octave map (an array indexed
  by pitch class that gives the octave to use for each pitch class).

  Example:
  (def pitch-oct-map [4 5 3 6 2 7 1 4 5 2 1 8])
  (gen-pitch 2 pitch-oct-map) => :D3"
  [pc octave-map]
  (keyword (str (name (pitch-names pc)) (octave-map pc))))

(defn map-pcs-to-pitches
  "Given a set of pitch classes (either numeric or named pitch classes) and an octave map,
  generates a sequence of octave-placed pitches as named pitches. The octave map is a
  list that maps each numeric pitch class to the appropriate octave where 4 represents
  the octave starting on middle C (C4 is middle C).

  Examples:
  (def pitch-oct-map [4 5 3 6 2 7 1 4 5 2 1 8])
  (map-set-to-octaves [0 1 2] pitch-oct-map) => (:C4 :Db5 :D3)
  (map-set-to-octaves [:C :Db :D] pitch-oct-map) => (:C4 :Db5 :D3)"
  [pc-set octave-map]
  (let [set-num (numeric-set pc-set)]
    (map gen-pitch set-num (repeat octave-map))))

(defn intervals
  "Returns the intervals between the pcs in the given pc set.

  Example:
  (intervals [0 3 5 9]) => (3 2 4 -9)"
  [pc-set]
  (->> (numeric-set pc-set)
       cycle
       (partition 2 1)
       (take (count pc-set))
       (map #(- (apply - %)))))

(defn interval-vector
  "Returns the interval vector for the given pc set. An interval vector has the cardinalities
  of intervals in the pc set.

  Example:
  (interval-vector [3 11 0 9 2]) => ([1 2] [2 2] [3 3] [4 1] [5 1] [6 1])"
  [pc-set]
  (letfn [(interval-for-vector [vals]
            (let [interval (Math/abs (apply - vals))]
              (if (> interval 6)
                (- 12 interval)
                interval)))]
    (->> (combo/combinations (numeric-set pc-set) 2)
         (map interval-for-vector)
         frequencies
         sort)))

(defn start-with-pc
  "Returns a pc set that starts with the given pc but otherwise has the same intervals as the
  given pc set.

  Example:

  (start-with-pc 3 [0 3 5 8]) => (3 6 8 11)
  (start-with-pc 6 [0 3 5 8]) => (6 9 11 2)"
  [start-pc pc-set]
  (let [set-num (numeric-set pc-set)
        first-pc (first set-num)]
    (transform set-num (- start-pc first-pc) transpose)))

(defn rotate
  "Rotates the given pc set through all the pcs, starting each rotation with the first pc in the given
  pc set.

  Example:

  (->> (rotate [:Eb :A :C :B :E :D])
       (map named-set)) =>
    ((:Eb :A :C :B :E :D)
    (:Eb :Gb :F :Bb :Ab :A)
    (:Eb :D :G :F :Gb :C)
    (:Eb :Ab :Gb :G :Db :E)
    (:Eb :Db :D :Ab :B :Bb)
    (:Eb :E :Bb :Db :C :F))"
  [pc-set]
  (let [count-pcs (count pc-set)
        set-num (numeric-set pc-set)
        first-pc (first set-num)]
    (->> (cycle set-num)
         (partition count-pcs 1)
         (take count-pcs)
         (map #(start-with-pc first-pc %)))))

(defn oscillate
  "Returns a lazy sequence of oscillations of the given pc set.

  Example:
  (take 12 (oscillate [0 1 3])) => (0 1 3 3 1 0 0 1 3 3 1 0)"
  [pc-set]
  (cycle (concat pc-set (reverse pc-set))))