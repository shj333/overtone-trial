(ns berwickheights.cac.overtone.microsound
  (:require [overtone.sc.envelope :as ot-env]
            [overtone.sc.buffer :as ot-buf]
            [berwickheights.cac.overtone.shapes :as shapes]))

(defn num-lin-lin
  ([x in-min in-max out-min out-max] (num-lin-lin x in-min in-max out-min out-max :min-max))
  ([x in-min in-max out-min out-max clip]
   (cond (and (not (nil? (clip #{:min-max :min}))) (<= x in-min)) out-min
         (and (not (nil? (clip #{:min-max :max}))) (>= x in-max)) out-max
         true (+ out-min (* (- out-max out-min) (/ (- x in-min) (- in-max in-min)))))))

(defn- make-sinc-point
  [sinc-num x length]
  (let [val (* (num-lin-lin x 0 (dec length) (- 0 Math/PI) Math/PI) sinc-num)]
    (/ (Math/sin val) val)))

(defn- make-sinc
  [sinc-num length]
  (map #(make-sinc-point sinc-num % length) (range length)))

(defn make-sincs
  [num-instances length]
  (into {} (for [idx (range 1 (inc num-instances))] [(keyword (str "sinc" idx)) (make-sinc idx length)])))


(def env-data {:sine     (ot-env/env-sine)
               :guass    (ot-env/envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :sin)
               :linear   (ot-env/envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :lin)
               :welch    (ot-env/envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :welch)
               :expodec  (ot-env/envelope [1, 0.001] [1] :exp)
               :rexpodec (ot-env/envelope [0.001, 1] [1] :exp)
               :perc1    (ot-env/env-perc 0.05 0.95)
               :perc2    (ot-env/env-perc 0.1 0.9)})

(def env-signals (merge (make-sincs 10 400)
                        (into {} (for [[k env] env-data] [k (shapes/env->signal env 400)]))))


(defn env->buffer
  [env-signals]
  (let [b (ot-buf/buffer (count env-signals))]
    (ot-buf/buffer-write! b env-signals)
    b))

(defn make-env-bufs [] (into {} (for [[k env] env-signals] [k (env->buffer env)])))
