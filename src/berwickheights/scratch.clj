(ns berwickheights.scratch)


; Lazy sequence of buffers
(def bufs (take 6 (repeatedly #(buffer 2048))))
(def buf-ids (map buffer-id bufs))
(doseq [buf-id buf-ids]
  (apply snd "/b_gen" buf-id "sine1" 7 (map / (range 1.0 (inc 6)))))
(demo 4 (apply * 0.0001 (map #(osc % (rand 1000)) buf-ids)))


; Using lazy sequence to generate multiple sine waves at random freqs btwn 0 and 1000
(demo 2 (apply * 0.4 (map #(sin-osc %) (take 5 (repeatedly #(rand 1000))))))


; Curvature of percussive envelope
(demo (* (env-gen (env-perc :attack 0.01 :release 2 :level 1 :curve -4)) (sin-osc)))
(demo (* (env-gen (env-perc :attack 0.01 :release 2 :level 1 :curve -8)) (sin-osc)))


; Some random octave maps for BHS CAC
(let [oct-map1 [5 4 3 4  2 5 4 3  4 4 5 3]
      oct-map2 [5 3 5 4  5 2 4 3  3 4 2 3]
      oct-map3 [5 3 5 4  5 2 4 3  1 2 5 3]]
  (def oct-maps (pt/oscillate [oct-map1 oct-map2 oct-map3])))

(let [oct-map1 [4 5 3 4  3 5 4 4  4 3 5 3]
      oct-map2 [4 3 5 4  5 3 4 3  3 3 2 3]]
  (def oct-maps (pt/oscillate [oct-map1 oct-map2])))

