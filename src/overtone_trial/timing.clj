(ns overtone-trial.timing
  (:use overtone.core)
  (:require [berwickheights.cac.play :as play]))


; (boot-external-server)
; (use 'overtone.live)

;
; Instruments
;
(definst gong [freq 440 oct-mult 1.0 dur 5 amp 0.2]
         (let [this-freq (* freq oct-mult)
               this-env (env-gen (env-perc 0.3 dur amp) :action FREE)]
           (* this-env (sin-osc this-freq))))

(defn play-gong-cluster [gong-data oct-mult]
  (doseq [item gong-data] (gong :freq (item :freq) :oct-mult oct-mult :dur (item :dur))))

(definst pitch-perc [cf 2000 amp 3.0 dur 1]
         (let [source (white-noise)
               bw (* 0.05 cf)
               rq (/ bw cf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (bpf source cf rq))))
;(pitch-perc :cf 100 :amp 100.0)  ; Timpani?
;(pitch-perc :cf 500 :amp 8.0)
;(pitch-perc :cf 1000 :amp 8.0)
;(pitch-perc :cf 2000 :amp 8.0)




;
; Music
;
(defn play-gong-seq [nome gong-data]
  (let [phrase [[0 [gong-data 0.5]]
                [1 [gong-data 0.25]]
                [2 [gong-data 0.3]]
                [3.5 [gong-data 0.4]]
                [3.75 [gong-data 0.14]]]]
    (play/play-phrase play-gong-cluster phrase nome)))

(defn play-perc-seq [nome]
  (let [phrase [[1 [:cf 2000 :amp 4.0]]
                [1.125 [:cf 2100 :amp 3.0]]
                [1.25 [:cf 2300 :amp 3.0]]
                [1.375 [:cf 1900 :amp 4.0]]
                [1.75 [:cf 2700 :amp 6.0]]
                [2.1 [:cf 1300 :amp 6.0]]]]
    (play/play-phrase pitch-perc phrase nome 0.6)))

(defn section1 [nome gong-freqs gong-durs]
  (let [gong-data (->> (map vector (repeat :freq) (map midi->hz gong-freqs) (repeat :dur) gong-durs)
                       (map #(apply hash-map %)))]
    (play-gong-seq nome gong-data)
    (play-perc-seq nome)))

(def nome (metronome 44))

; (section1 nome [74 61 70 81 76] [8 6 10 12 5])

;(let [lowest-gong-midi 70
;      freqs (map + (repeat lowest-gong-midi) (range))
;      durs [8 6 10 12 5]]
;  (section1 nome freqs durs))
