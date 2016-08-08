(ns berwickheights.cac.overtone.microsound
  (:require [overtone.core :as ot]
            [berwickheights.cac.overtone.shapes :as shapes]
            [berwickheights.cac.probability :as prob]))

;
; Grain envelopes
;
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

(defn- make-sincs
  [num-instances length]
  (into {} (for [idx (range 1 (inc num-instances))] [(keyword (str "sinc" idx)) (make-sinc idx length)])))


(def env-data {:guass       (ot/env-sine)
               :quasi-guass (ot/envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :sin)
               :linear      (ot/envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :lin)
               :welch       (ot/envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :welch)
               :expodec     (ot/envelope [1, 0.001] [1] :exp)
               :rexpodec    (ot/envelope [0.001, 1] [1] :exp)
               :perc1       (ot/env-perc 0.05 0.95)
               :perc2       (ot/env-perc 0.1 0.9)})

(def env-signals (merge (make-sincs 10 400)
                        (into {} (for [[k env] env-data] [k (shapes/env->signal env 400)]))))


;
; Buffers for grain envelopes that can be used in synths
;
(defn env->buffer
  [env-signals]
  (let [b (ot/buffer (count env-signals))]
    (ot/buffer-write! b env-signals)
    b))

(defn make-env-bufs [] (into {} (for [[k env] env-signals] [k (env->buffer env)])))



;
; Triggers
;
(ot/defsynth sync-trigger [out 0 density 1] (ot/out:kr out (ot/impulse:kr density)))
(ot/defsynth async-trigger [out 0 density 1] (ot/out:kr out (ot/dust:kr density)))
(ot/defsynth coin-trigger [out 0 density 1 prob 0.5] (ot/out:kr out (ot/coin-gate:kr prob (ot/impulse:kr density))))


;
; Pans
;
(ot/defsynth rand-pan [out 0 density 1] (ot/out:kr out (ot/lf-noise0:kr density)))
(ot/defsynth const-pan [out 0 pan 0] (ot/out:kr out pan))



;
; Sets density of given instruments to random values over time
;
(defn set-density
  ([insts] (set-density insts (+ (ot/now) 1000)))
  ([insts cur-time] (set-density insts cur-time 2 20))
  ([insts cur-time low high]
   (let [density (prob/exprand low high)
         wait-time (/ 1000.0 density)
         next-time (+ cur-time wait-time)]
     ; (println "Density: " density ", Wait: " wait-time ", Insts: " insts)
     (doseq [inst insts] (ot/at cur-time (ot/ctl inst :density density)))
     (ot/apply-by next-time #'set-density [insts next-time]))))


;
; Creates busses and instruments for triggers and pans that drive grain synths
;
(defn- make-busses-insts
  [defs bus-type]
  (let [busses (into {} (for [key (keys defs)] [key (bus-type)]))
        insts (into {} (for [[key bus] busses] [key ((key defs) :out bus)]))]
    {:busses busses :insts insts}))

(def core-trigger-defs {:sync      sync-trigger
                        :rand-sync sync-trigger
                        :async     async-trigger
                        :coin      coin-trigger})

(def core-pan-defs {:left         const-pan
                    :center-left  const-pan
                    :center       const-pan
                    :center-right const-pan
                    :right        const-pan})

(defn make-triggers-pans
  ([] (make-triggers-pans {}))
  ([trigger-defs]
   (let [all-trigger-defs (merge core-trigger-defs trigger-defs)
         trigger-result (make-busses-insts all-trigger-defs ot/control-bus)
         triggers (:insts trigger-result)
         pan-defs (into {} (for [k (keys all-trigger-defs)] [k rand-pan]))
         all-pan-defs (merge core-pan-defs pan-defs)
         pan-result (make-busses-insts all-pan-defs ot/control-bus)
         pans (:insts pan-result)]
     (ot/ctl (:left pans) :pan -1)
     (ot/ctl (:center-left pans) :pan -0.5)
     (ot/ctl (:center pans) :pan 0)
     (ot/ctl (:center-right pans) :pan 0.5)
     (ot/ctl (:right pans) :pan 1)
     (set-density [(:rand-sync triggers) (:rand-sync pans)])
     {:triggers trigger-result :pans pan-result})))
