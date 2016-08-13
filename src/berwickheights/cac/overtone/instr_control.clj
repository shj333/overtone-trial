(ns berwickheights.cac.overtone.instr-control
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.fx :as fx]))


(defonce ^:private sound-defs (atom {}))
(defonce ^:private instrs (atom {}))


(defn play-instr
  [instr-key synth params]
  (println "Playing instr" instr-key)
  (swap! instrs assoc instr-key (apply synth params)))

(defn stop-instr
  ([instr-key] (stop-instr instr-key 10))
  ([instr-key num-incrs]
   (println "Stopping instr" instr-key "over" num-incrs "increments")
   (let [this-instr (@instrs instr-key)
         start-amp (ot/node-get-control this-instr :amp)
         amp-delta (/ start-amp num-incrs)
         amps (reverse (take num-incrs (range 0 start-amp amp-delta)))
         start-time (+ (ot/now) 500)
         time-delta 250
         times (take num-incrs (iterate #(+ time-delta %) start-time))
         amps-times (map vector amps times)]
     (doseq [[amp time] amps-times] (ot/at time (ot/ctl this-instr :amp amp)))
     (ot/at (+ time-delta (last times)) (ot/kill this-instr)))))



(defn play-sound
  ([time instr-key] (play-sound time instr-key instr-key))
  ([time instr-key sound-def-key]
   (let [{:keys [synth params]} (sound-def-key @sound-defs)]
     (ot/apply-by time #(ot/at time (play-instr instr-key synth params))))))

(defn stop-sound
  [time instr-key]
  (ot/apply-by time #'stop-instr [instr-key]))

(defn set-amp
  [time instr-key amp]
  (ot/apply-by time #(ot/at time
                            (println "Set amp for instr" instr-key "to" amp)
                            (ot/ctl (@instrs instr-key) :amp amp))))


(defn- param-data
  [key data]
  (case key
    :env-buf (micro/env-buf data)
    :trigger-bus (micro/trigger-bus data)
    :pan-bus (micro/pan-bus data)
    :out (fx/bus data)
    data))

(defn- define-sound
  [sound-def-key sound-def-data]
  (let [{:keys [synth params]} sound-def-data
        params-list (->> (flatten (for [[key data] params] [key (param-data key data)]))
                         (cons [:tail (fx/group :producer-grp)]))
        sound-def {:synth synth :params params-list}]
    [sound-def-key sound-def]))

(defn define-sounds
  [sound-defs-data]
  (swap! sound-defs merge (into {} (for [[sound-def-key sound-def-data] sound-defs-data] (define-sound sound-def-key sound-def-data)))))
