(ns overtone-test.time-test
  (use overtone.live))

;
; Instruments
;
(definst gong [freq 440 oct-mult 1.0 dur 5 amp 0.2]
         (let [this-freq (* freq oct-mult)
               this-env (env-gen (env-perc 0.3 dur amp) :action FREE)]
           (* this-env (sin-osc this-freq))))

(defn play-gong-cluster [gong-data oct-mult]
  (doseq [item gong-data] (gong2 :freq (item :freq) :oct-mult oct-mult :dur (item :dur))))

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
  (let [beat (nome)]
    (at (nome beat) (play-gong-cluster gong-data 0.5))
    (at (nome (+ 1 beat)) (play-gong-cluster gong-data 0.25))
    (at (nome (+ 2 beat)) (play-gong-cluster gong-data 0.3))
    (at (nome (+ 3.5 beat)) (play-gong-cluster gong-data 0.4))
    (at (nome (+ 3.75 beat)) (play-gong-cluster gong-data 0.14))))

(defn play-perc-seq [nome]
  (let [beat (+ (nome) 0.6)]
    (at (nome (+ 1 beat)) (pitch-perc :cf 2000 :amp 4.0))
    (at (nome (+ 1.125 beat)) (pitch-perc :cf 2100 :amp 3.0))
    (at (nome (+ 1.25 beat)) (pitch-perc :cf 2300 :amp 3.0))
    (at (nome (+ 1.375 beat)) (pitch-perc :cf 1900 :amp 4.0))
    (at (nome (+ 1.75 beat)) (pitch-perc :cf 2700 :amp 6.0))
    (at (nome (+ 2.1 beat)) (pitch-perc :cf 1300 :amp 6.0))))

(defn section1 [nome gong-data]
  (play-gong-seq nome gong-data)
  (play-perc-seq nome))

(def nome (metronome 44))
(let [gong-data [{:freq (midi->hz 74) :dur 8}
                 {:freq (midi->hz 61) :dur 6}
                 {:freq (midi->hz 70) :dur 10}
                 {:freq (midi->hz 81) :dur 12}
                 {:freq (midi->hz 76) :dur 5}]]
  (section1 nome gong-data))
(let [lowest-gong-midi 70
      gong-data (map #(identity { :freq (midi->hz (+ lowest-gong-midi %)) :dur 8 }) (take 5 (range)))]
  (section1 nome gong-data))
