(ns overtone-test.gran-synth
  (:use overtone.live))

(defsynth gran-synth-test [out-chan 0]
  (let [src (grain-buf )]
  (out out-chan (* 0.2 src))))



