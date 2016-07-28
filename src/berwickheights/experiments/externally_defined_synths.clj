(ns berwickheights.experiments.externally-defined-synths
  (:use [overtone.core]))


; When using synths defined in SCLang on external server
(let [node-id (next-id :node)]
  (snd "/s_new" "sine" node-id)
  (Thread/sleep 2000)
  (snd "/n_free" node-id)
  )

(snd "/s_new" "sine2" -1 0 1 "freq" 200.0 "dur" 2.0)
