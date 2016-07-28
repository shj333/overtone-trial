(ns overtone-trial.plotting
  (:use overtone.live)
  (:require [incanter core charts datasets])
  (:require [berwickheights.cac.overtone.shapes :as shapes]))


(defn make-buffer [f]
  (let [s 1024
        b (buffer s)]
    (buffer-write! b (map #(f (/ (* % 2 Math/PI) s)) (range 0 s)))
    b))

(def buf1 (make-buffer #(Math/sin %)))
(def buf-floats (into '() (buffer-read buf1)))
(incanter.core/view (incanter.charts/xy-plot (range 1024) buf-floats))


(def env-buf (buffer 1024))
(definst env-buf [buf-id 0]
         (let [env (env-gen (env-perc))
               scale (buf-rate-scale:kr buf-id)
               frames (buf-frames:kr buf-id)
               phase (phasor:ar 0 scale 0 frames)]
           (buf-wr:ar env buf-id phase)))
(env-buf (buffer-id env-buf))
(stop)
(def buf-floats (into '() (buffer-read env-buf)))
(incanter.core/view (incanter.charts/xy-plot (range 1024) buf-floats))

(def buf-sine (env-gen (env-sine)))
(def buf-floats (into '() (buffer-read buf-sine)))
(incanter.core/view (incanter.charts/xy-plot (range 1024) buf-floats))


(let [s 20
      b (buffer s)
      env (shapes/env->signal (env-sine) s)]
  (buffer-write! b env)
  (incanter.core/view (incanter.charts/xy-plot (range s) env)))

(shapes/env->signal (sine) 20)
