(ns berwickheights.pitch-transforms)

(def pitch-names [:C :Db :D :Eb :E :F :Gb :G :Ab :A :Bb :B])
(def pitch-nums { :C 0 :Db 1 :D 2 :Eb 3 :E 4 :F 5 :Gb 6 :G 7 :Ab 8 :A 9 :Bb 10 :B 11})

(defn transpose-pitch [pitch level] (mod (+ pitch level) 12))
(defn transpose [set level] (map #(transpose-pitch % level) set))
(defn transpose-many [set levels] (map #(transpose set %) levels))
(defn boulez-mult [set1 set2]
  (let [set1-num (map pitch-nums set1)
        set2-num (map pitch-nums set2)
        first-pitch (first set2-num)
        set2-zeroed (map #(- % first-pitch) set2-num)]
    (sort (distinct (apply concat (transpose-many set1-num set2-zeroed))))))

; From Boulez On Music Today, page 40
(def example-multi-set [:Ab :C :A :B])
(map pitch-names (transpose (boulez-mult [:D :F :Eb] example-multi-set) 6))
(map pitch-names (transpose (boulez-mult [:Bb :Db] example-multi-set) 6))
(map pitch-names (transpose (boulez-mult example-multi-set example-multi-set) 6))
(map pitch-names (transpose (boulez-mult [:E :G] example-multi-set) 6))
(map pitch-names (transpose (boulez-mult [:Gb] example-multi-set) 6))

(map pitch-names (transpose (boulez-mult [:D :F :Eb] example-multi-set) 9))
(map pitch-names (transpose (boulez-mult [:Bb :Db] example-multi-set) 9))
(map pitch-names (transpose (boulez-mult example-multi-set example-multi-set) 9))
(map pitch-names (transpose (boulez-mult [:E :G] example-multi-set) 9))
(map pitch-names (transpose (boulez-mult [:Gb] example-multi-set) 9))

(map pitch-names (transpose (boulez-mult [:D :F :Eb] example-multi-set) 7))
(map pitch-names (transpose (boulez-mult [:Bb :Db] example-multi-set) 7))
(map pitch-names (transpose (boulez-mult example-multi-set example-multi-set) 7))
(map pitch-names (transpose (boulez-mult [:E :G] example-multi-set) 7))
(map pitch-names (transpose (boulez-mult [:Gb] example-multi-set) 7))
