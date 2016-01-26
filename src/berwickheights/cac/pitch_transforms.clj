(ns berwickheights.cac.pitch-transforms)

(def pitch-names [:C :Db :D :Eb :E :F :Gb :G :Ab :A :Bb :B])
(def pitch-nums {:C 0 :Db 1 :D 2 :Eb 3 :E 4 :F 5 :Gb 6 :G 7 :Ab 8 :A 9 :Bb 10 :B 11})
(defn numeric-set [set] (if (keyword? (first set)) (map pitch-nums set) set))
(defn named-set [set] (if (keyword? (first set)) set (map pitch-names set)))


(defn apply-one [set level f] (map #(f % level) set))
(defn apply-many [set levels f] (map #(f set %) levels))

(defn transpose-pitch [pitch level] (mod (+ pitch level) 12))
(defn transpose [set level] (apply-one set level transpose-pitch))
(defn transpose-many [set levels] (apply-many set levels transpose))

(defn invert-pitch [pitch level] (mod (+ (- 12 pitch) level) 12))
(defn invert [set level] (apply-one set level invert-pitch))
(defn invert-many [set levels] (apply-many set levels invert))

(defn retro [set level] (-> (transpose set level) reverse))
(defn retro-many [set levels] (map #(retro set %) levels))

(defn boulez-multi [set1 set2]
  (let [set1-num (numeric-set set1)
        set2-num (numeric-set set2)
        first-pitch (first set2-num)
        set2-zeroed (map #(- % first-pitch) set2-num)]
    (-> (transpose-many set1-num set2-zeroed)
        flatten
        distinct
        sort)))

(defn gen-pitch-octave [pc octave-map] (-> (name (pitch-names pc)) (str (octave-map pc)) keyword))
(defn map-set-to-octaves
  "Given a set of pitch classes (either numeric or named pitch classes) and an octave map,
  generates a sequence of octave-placed pitches as named pitches. The octave map is a
  list that maps each numeric pitch class to the appropriate octave where 4 represents
  the octave starting on middle C (C4 is middle C).

  Examples:
  (def pitch-oct-map [4 5 3 6 2 7 1 4 5 2 1 8])
  (map-set-to-octaves [0 1 2] pitch-oct-map) => (:C4 :Db5 :D3)
  (map-set-to-octaves [:C :Db :D] pitch-oct-map) => (:C4 :Db5 :D3)"
  [set octave-map]
  (let [set-num (numeric-set set)]
    (map #(gen-pitch-octave % octave-map) set-num)))