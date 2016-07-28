(ns overtone-trial.jit
  (:use overtone.live))

(def jit-busses (atom {}))

(defn create-node-proxy:kr [name num-channels]
  (let [bus (control-bus num-channels name)]
    (swap! jit-busses assoc name bus)
    ; TODO Should return a node-proxy instance
    true))

(defn set-node-proxy-src:kr [name source]
  ; TODO Figure out how to assign output bus for given source (this assumes first arg)
  (source (@jit-busses name))
  true)

(defn get-node-proxy:kr [name]
  (in:kr (name @jit-busses)))



(create-node-proxy:kr :x 1)

(defsynth foo-src [out-bus 0]
          (out:kr out-bus (lf-pulse:kr 1.3)))
(set-node-proxy-src:kr :x foo-src)


(defsynth foo-sin [out-bus 0]
          (let [freq (+ 300 (* 200 (get-node-proxy:kr :x)))]
            (out:ar out-bus (* 0.1 (sin-osc freq)))))
(foo-sin)

(stop)
