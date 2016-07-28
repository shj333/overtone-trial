(ns berwickheights.cac.overtone.patterns
  (:require [overtone.core :as ot]))

(defn- get-value
  [f param]
  (if (seq? param)
    (f param)
    param))

(defn- this-synth-params
  [params-map]
  (flatten (for [[k v] params-map] [k (get-value first v)])))

(defn- next-synth-params
  [params-map]
  (into {} (for [[k v] params-map] [k (get-value next v)])))

(defn defpbind
  [beat data]
  (let [{:keys [synth params]} @data
        this-synth (get-value first synth)
        this-params (this-synth-params params)
        dur (get-value first (:dur params))
        next-beat (+ beat dur)]
    ;(println (pr-str "beat: " beat ", next-beat: " next-beat))
    (when (not-any? nil? this-params)
      (ot/at beat (apply this-synth this-params))
      (swap! data assoc :synth (get-value next synth) :params (next-synth-params params))
      (ot/apply-by next-beat #'defpbind [next-beat data]))))

(defn defpat
  [data & args]
  (swap! data assoc :params (apply assoc (:params @data) args))
  true)

(defn pat-key
  [data key]
  (key (:params @data)))


