(ns berwickheights.cac.overtone.instr-control
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.microsound :as micro]
            [berwickheights.cac.overtone.fx :as fx]))


(defonce ^:private sound-defs (atom {}))
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

(defn play-sound
  [start-time time-offset instr-key sound-def-key]
  (let [sound-def (sound-def-key @sound-defs)]
    (play-instr-at (+ start-time time-offset) instr-key (:synth sound-def) (:params sound-def))))

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
  (println "Set amp for instr" instr-key "to" amp)
  (ot/ctl (@instrs instr-key) :amp amp))


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
