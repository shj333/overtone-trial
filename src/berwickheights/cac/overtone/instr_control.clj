(ns berwickheights.cac.overtone.instr-control
  (:require [overtone.core :as ot]))


(defonce ^:private instrs (atom {}))

(defn instr
  [instr-key]
  (@instrs instr-key))

(defn play-instr
  [instr-key instr-def params]
  (println "Playing instr" instr-key)
  (let [instr (apply instr-def params)]
    (swap! instrs assoc instr-key instr)))

(defn play-instr-at
  [time instr-key instr-def params]
  (ot/apply-by time #(ot/at time (play-instr instr-key instr-def params))))

(defn stop-instr
  ([instr-key] (stop-instr instr-key 10))
  ([instr-key num-incrs]
   (println "Stopping instr" instr-key "over" num-incrs "increments")
   (let [this-instr (instr instr-key)
         start-amp (ot/node-get-control this-instr :amp)
         amp-delta (/ start-amp num-incrs)
         amps (reverse (take num-incrs (range 0 start-amp amp-delta)))
         start-time (+ (ot/now) 500)
         time-delta 250
         times (take num-incrs (iterate #(+ time-delta %) start-time))
         amps-times (map vector amps times)]
     (doseq [[amp time] amps-times] (ot/at time (ot/ctl this-instr :amp amp)))
     (ot/at (+ time-delta (last times)) (ot/kill this-instr)))))

(defn set-amp
  [instr-key amp]
  (ot/ctl (@instrs instr-key) :amp amp))
