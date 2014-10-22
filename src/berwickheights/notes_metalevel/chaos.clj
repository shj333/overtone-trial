(ns
  ^{:author stuart}
  berwickheights.notes_metalevel.chaos)

(defn logistic-map
  "Returns lazy seq according to y(s, c) = c * y * (1 - y). From Chapter 17 of Notes from the Metalevel."
  ([chaos] (logistic-map (rand 1.0) chaos))
  ([seed chaos]
    (lazy-seq
      (let [new-val (* seed chaos (- 1 seed))]
        (cons new-val (logistic-map new-val chaos))))))

(defn lazy-freqs
  "Given a lazy seq, returns the next count freqs in sequence"
  [lazy-s low high low-freq high-freq count]
  (map #(overtone.algo.scaling/scale-range % low high low-freq high-freq) (take count lazy-s)))

(def logistic-map-freqs (lazy-freqs (logistic-map 3.7) 0 1.0 40 100 1000))

(definst sawzall [freq 440 amp 0.2]
  (* amp (env-gen (perc 0.1 0.8) :action FREE) (saw freq)))

(defn play-freqs [t beat-dur freqs amp 0.2]
  (when freqs
    (let [next-beat (+ t beat-dur)]
      (at t (sawzall (first freqs) amp))
      (apply-by next-beat #'play-freqs next-beat beat-dur (next freqs) amp []))))

(play-freqs (now) 50 logistic-map-freqs)
