(ns berwickheights.cac.chaos)

; See https://en.wikipedia.org/wiki/Logistic_map
(defn logistic-map
  "Returns lazy seq according to new-val = r * old-val * (1 - old-val). From Chapter 17 of Notes from the Metalevel.
  R vals:
    1-3: converges to one note
    3-3.45: converges to two notes
    3.45-3.54: converges to four notes
    3.54-3.56: converges to 8, 16, 32 notes
    > 3.56: chaos"
  ([r] (logistic-map (rand 1.0) r))
  ([prev-val r]
   (lazy-seq
     (let [new-val (* r prev-val (- 1 prev-val))]
       (cons new-val (logistic-map new-val r))))))


; See https://en.wikipedia.org/wiki/H%C3%A9non_map
(defn henon-map
  "Returns lazy seq of duples according to H(x, y) = ((y + 1) - ax2, bx), where a and b are constants (typically
  1.4 and 0.3). From Chapter 17 of Notes from the Metalevel."
  ([] (henon-map 1.4 0.3))
  ([a b] (henon-map a b 0 0))
  ([x y a b]
   (lazy-seq
     (let [new-x (- (+ y 1) (* a x x))
           new-y (* b x)]
       (cons [new-x new-y] (henon-map a b new-x new-y))))))
