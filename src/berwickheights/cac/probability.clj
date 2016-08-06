(ns berwickheights.cac.probability
  (:import (org.apache.commons.math3.random RandomDataGenerator)))

(defmulti gen-rands
          "Generates a lazy sequence of random numbers with a distribution defined by the given dispatch.

          Examples:
          (gen-rands :uniform) => Lazy seq with mean distribution of 0.5
          (gen-rands :low-pass 2) => Lazy seq with mean distribution of 0.333
          (gen-rands :low-pass 3) => Lazy seq with mean distribution of 0.25
          (gen-rands :low-pass 4) => Lazy seq with mean distribution of 0.20
          (gen-rands :high-pass 2) => Lazy seq with mean distribution of 0.667
          (gen-rands :high-pass 3) => Lazy seq with mean distribution of 0.75
          (gen-rands :high-pass 4) => Lazy seq with mean distribution of 0.80"
          {:arglists '([dist & args])}
          (fn [dist & args] dist))

(defn- gen-n-rands [count] (take count (repeatedly rand)))

(defmethod gen-rands :uniform [dist] (repeatedly rand))
(defmethod gen-rands :low-pass [dist count] (repeatedly #(apply min (gen-n-rands count))))
(defmethod gen-rands :high-pass [dist count] (repeatedly #(apply max (gen-n-rands count))))
(defmethod gen-rands :mid-pass [dist] (repeatedly #(/ (reduce + (gen-n-rands 2)) 2)))
(defmethod gen-rands :exponential [dist lambda] (repeatedly #(/ (- (Math/log (- 1 (rand)))) lambda)))


; Test Code
(def rand-gen (RandomDataGenerator.))
(.nextExponential rand-gen 1.0)

(defn exprand
  [low high]
  (* low (Math/exp (* (Math/log (/ high low)) (rand)))))