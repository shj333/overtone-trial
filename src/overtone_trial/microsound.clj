(ns overtone-trial.microsound
  (:use overtone.live)
  (:require [incanter core charts datasets])
  (:require [berwickheights.cac.overtone.shapes :as shapes]))

;switch(clip,
;\minmax, {
;          if (this <= inMin, {^outMin}) ;
;          if (this >= inMax, {^outMax}) ;
;          },
;\min, {
;       if (this <= inMin, {^outMin}) ;
;       },
;\max, {
;       if (this >= inMax, {^outMax}) ;
;       }
;) ;
; (this-inMin)/(inMax-inMin) * (outMax-outMin) + outMin
; ^pow(outMax/outMin, (this-inMin) / (inMax-inMin)) * outMin



;Env.sine, // Guassian
;Env ([0, 1, 1, 0], [0.33, 0.34, 0.33], \sin), // Quasi-Guassian
;Env ([0, 1, 1, 0], [0.33, 0.34, 0.33], \lin), // Linear
;Env ([0, 1, 1, 0], [0.33, 0.34, 0.33], \welch), // Welch
;Env ([1, 0.001], [1], \exp), // Expodec
;Env ([0.001, 1], [1], \exp), // Rexpodec
;Env.perc (0.05, 0.95), // Percussive 1
;Env.perc (0.1, 0.9) // Percussive 2

;local.makeSinc = { |local, num=1, size=400| dup({ |x| x = x.linlin(0, size-1, -pi, pi) * num; sin(x) / x }, size) };
;local.makeSincs = {|local, num=1| dup ({|x| local.makeSinc (x + 1)}, num)

(defn num-lin-lin
  ([x in-min in-max out-min out-max] (num-lin-lin x in-min in-max out-min out-max :min-max))
  ([x in-min in-max out-min out-max clip]
   (cond (and (not (nil? (clip #{:min-max :min}))) (<= x in-min)) out-min
         (and (not (nil? (clip #{:min-max :max}))) (>= x in-max)) out-max
         true (+ out-min (* (- out-max out-min) (/ (- x in-min) (- in-max in-min)))))))

(defn make-sincs
  [num-instances length]
  (letfn [(make-one-point
            [sinc-num x length]
            (let [val (* (num-lin-lin x 0 (dec length) (- 0 Math/PI) Math/PI) sinc-num)]
              (/ (Math/sin val) val)))
          (make-sinc [sinc-num length]
            (map #(make-one-point sinc-num % length) (range length)))]
    (map #(make-sinc (inc %) length) (range num-instances))))

(let [env (last (make-sincs 10 400))
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))


(defn env->buffer
  [env-signals]
  (let [b (buffer (count env-signals))]
    (buffer-write! b env-signals)
    b))

(def env-data {:sine        (env-sine)
               :quasi-guass (envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :sin)
               :linear      (envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :lin)
               :welch       (envelope [0, 1, 1, 0] [0.33, 0.34, 0.33] :welch)
               :expodec     (envelope [1, 0.001] [1] :exp)
               :rexpodec    (envelope [0.001, 1] [1] :exp)
               :perc1       (env-perc 0.05 0.95)
               :perc2       (env-perc 0.1 0.9)})

(def env-signals (into {} (for [[k env] env-data] [k (shapes/env->signal env 400)])))

(def env-bufs (into {} (for [[k env] env-signals] [k (env->buffer env)])))

(let [env (:perc2 env-signals)
      length (count env)]
  (incanter.core/view (incanter.charts/xy-plot (range length) env)))


; |envbuf = -1, density = 10, graindur = 0.1, freq = 440, freqdevnoise = 400, amp = 0.05, pan = 0|
; // use WhiteNoise to control deviation from center
; var freqdev = WhiteNoise.kr (freqdevnoise) ;
; var freqs = freq + freqdev ;
; // var freqs = [1.0, 0.6, 0.7, 0.8, 0.9, 1.1, 1.2, 2.05, 3.07, 4.7] .collect {|n| (freq + freqdev) * n} ;
; var trig = Impulse.kr (density) ;
; // var trig = CoinGate.kr (0.3, Impulse.kr (density)) ;
; // var trig = Dust.kr (density) ;
; GrainSin.ar (2, trig, graindur, freqs, pan, envbuf) * amp
(definst my-grain-sin [out 0 env-buf -1 trigger-bus 0 grain-dur 0.1 freq 440 freq-dev-noise 400 amp 0.05]
         (let [[trigger pan] (in:kr trigger-bus 2)
               freq-dev (* (white-noise:kr) freq-dev-noise)
               this-freq (+ freq freq-dev)]
           (out:ar out (* amp (grain-sin:ar 2 trigger, grain-dur this-freq pan env-buf)))))

(def trigger-defs {:sync (definst sync-trigger [out 0 density 1] (out:kr out [(impulse:kr density) (lf-noise0:kr density)]))
                   :async (definst async-trigger [out 0 density 1] (out:kr out [(dust:kr density) (lf-noise0:kr density)]))
                   :coin (definst coin-trigger [out 0 density 1 prob 0.5] (out:kr out [(coin-gate:kr prob (impulse:kr density)) (lf-noise0:kr density)]))})

(def trigger-busses (into {} (for [key [:sync :async :coin]] [key (control-bus 2)])))
(def triggers (into {} (for [[key bus] trigger-busses] [key ((key trigger-defs) :out bus)])))
(for [[k v] triggers] [k (class v)])


(def grain-inst (my-grain-sin :env-buf (buffer-id (:perc1 env-bufs))
                              :trigger-bus (:sync trigger-busses)
                              :amp 0.2))
(ctl grain-inst :trigger-bus (:sync trigger-busses))
(ctl grain-inst :env-buf (buffer-id (:sine env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:perc2 env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:expodec env-bufs)))
(ctl grain-inst :env-buf (buffer-id (:rexpodec env-bufs)))

(stop)
