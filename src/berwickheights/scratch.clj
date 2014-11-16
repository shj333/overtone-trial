(ns berwickheights.scratch)


# Lazy sequence of buffers
(def bufs (take 6 (repeatedly #(buffer 2048))))
(def buf-ids (map buffer-id bufs))
(doseq [buf-id buf-ids]
  (apply snd "/b_gen" buf-id "sine1" 4 (map / (range 1.0 (inc 6)))))
(demo 4 (apply * 0.0001 (map #(osc % (rand 1000)) buf-ids)))


ß