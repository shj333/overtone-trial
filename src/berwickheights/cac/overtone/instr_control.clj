(ns berwickheights.cac.overtone.instr-control
  (:require [overtone.core :as ot]))


(defonce ^:private instrs (atom {}))

(defn play-instr
  [instr-key instr-def & params]
  (let [instr (apply instr-def params)]
    (swap! instrs assoc instr-key instr)))

(defn stop-instr
  [instr-key]
  ; TODO Decrease amplitude to zero over 10 secs, then kill instr
  (ot/kill (@instrs instr-key)))

(defn set-amp
  [instr-key amp]
  (ot/ctl (@instrs instr-key) :amp amp))
