(ns berwickheights.sc-book.chapter16-microsound
  (:use [overtone.core]))

; Gabor grain, Gaussian-shaped envelope
(defsynth gabor [out-bus [0 :ir] freq [440 :ir] sustain [1 :ir] pan [0.0 :ir] amp [0.1 :ir] width [0.25 :ir]]
          (let [env (lf-gauss:ar sustain width :loop 0 :action FREE)
                half-pi (* 0.5 (. Math PI))
                son (* env (f-sin-osc:ar freq half-pi))]
            (offset-out:ar out-bus (pan2:ar son pan amp))))
; (gabor)

; Approximation of above with a sine-shaped envelope
(defsynth gabor1 [out-bus [0 :ir] freq [440 :ir] sustain [1 :ir] pan [0.0 :ir] amp [0.1 :ir]]
          (let [snd (f-sin-osc:ar freq)
                env (env-gen:ar (env-sine sustain amp) :action FREE)]
            (offset-out:ar out-bus (pan2:ar (* snd env) amp))))
; (gabor1)


(defn- get-value
  [f param]
  (if (seq? param)
    (f param)
    param))

(defn this-synth-params
  [params-map]
  (flatten (for [[k v] params-map] [k (get-value first v)])))

(defn next-synth-params
  [params-map]
  (into {} (for [[k v] params-map] [k (get-value next v)])))

(defn defpbind
  [beat data]
  (let [{:keys [synth params dur]} @data
        this-synth (get-value first synth)
        this-params (this-synth-params params)
        next-beat (+ beat dur)]
    (println (pr-str "beat: " beat ", next-beat: " next-beat))
    (when (not-any? nil? this-params)
      (at beat (apply this-synth this-params))
      (swap! data assoc :synth (get-value next synth) :params (next-synth-params params))
      (apply-by next-beat #'defpbind [next-beat data]))))

(defn defpat
  [data & args]
  (swap! data assoc :params (apply assoc (:params @data) args))
  key)

(def gabor-data (atom {:synth  gabor
                       :params {:out-bus 0
                                :freq    440
                                :sustain (cycle [0.001 0.1])
                                :pan     0.0
                                :amp     (cycle [0.1, 0.1])
                                :width   0.25
                                }
                       :dur    1000
                       }))

(defpbind (now) gabor-data)
; Short grain 2x louder
(defpat gabor-data :sustain (cycle [0.001 0.1]) :amp (cycle [0.2, 0.1]))
; Short grain 4x louder
(defpat gabor-data :sustain (cycle [0.001 0.1]) :amp (cycle [0.4, 0.1]))
; Stop it
(defpat gabor-data :amp nil)
