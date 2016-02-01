(ns berwickheights.cac.probability)

(defmulti gen-rands
          "Generates a lazy sequence of random numbers with a distribution defined by the given dispatch.

          Examples:
          (gen-rands :uniform) => Lazy seq with mean distribution of 0.5
          (gen-rands :min 2) => Lazy seq with mean distribution of 0.333
          (gen-rands :min 3) => Lazy seq with mean distribution of 0.25
          (gen-rands :min 4) => Lazy seq with mean distribution of 0.20
          (gen-rands :max 2) => Lazy seq with mean distribution of 0.667
          (gen-rands :max 3) => Lazy seq with mean distribution of 0.75
          (gen-rands :max 4) => Lazy seq with mean distribution of 0.80"
          {:arglists '([dist & args])}
          (fn [dist & args] dist))

(defmethod gen-rands :uniform [dist] (repeatedly rand))
(defmethod gen-rands :min [dist count] (repeatedly #(apply min (take count (repeatedly rand)))))
(defmethod gen-rands :max [dist count] (repeatedly #(apply max (take count (repeatedly rand)))))
