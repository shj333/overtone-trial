(ns berwickheights.pitch-transforms)

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

; From Boulez On Music Today, page 40
(def example-multi-src-set [:Ab :C :A :B])
(def example-multi-parts [[:D :F :Eb] [:Bb :Db] example-multi-src-set [:E :G] [:Gb]])
(defn boulez-example [set transpose-level]
  (-> (boulez-multi set example-multi-src-set)
      (transpose transpose-level)
      named-set))
(map #(boulez-example % 6) example-multi-parts)
(map #(boulez-example % 9) example-multi-parts)
(map #(boulez-example % 7) example-multi-parts)


(defn map-pitch-to-freq [pc pitch-oct-map]
  (let [midi (+ 60 pc)]
    (+ midi (* 12 (pitch-oct-map pc)))))

(defn map-set-to-freq [set pitch-oct-map]
  (let [set-num (numeric-set set)]
    (map #(map-pitch-to-freq % pitch-oct-map) set-num)))

(def pitch-oct-map1 [0 1 -1 2 -2 3 -3 0 1 -2 -3 -4])