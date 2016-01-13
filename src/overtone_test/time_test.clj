(ns
  ^{:author stuart}
  overtone_test
  (use overtone.live))

(definst gong [amp 0.2 oct-mult 1.0]
         (let [data [{:freq (midi->hz 74) :dur 8}
                     {:freq (midi->hz 61) :dur 6}
                     {:freq (midi->hz 70) :dur 10}
                     {:freq (midi->hz 81) :dur 12}
                     {:freq (midi->hz 76) :dur 5}
                     ]
               max-dur (apply max (map :dur data))
               release-env (env-gen (envelope [1 1 0] [max-dur 0.0001] :linear) :action FREE)]
           (* release-env (reduce + (map #(let [this-freq (* (% :freq) oct-mult)
                                                this-env (env-gen (env-perc 0.3 (% :dur) amp))]
                                           (* this-env (sin-osc this-freq))) data)))))
(definst pitch-perc [cf 2000 amp 3.0 dur 1]
         (let [source (white-noise)
               bw (* 0.05 cf)
               rq (/ bw cf)
               env (env-gen (env-perc :release dur) :action FREE)]
           (* amp env (bpf source cf rq))))

(defn play-gong [nome]
  (let [beat (nome)]
    (at (nome beat) (gong :oct-mult 0.5))
    (at (nome (+ 1 beat)) (gong :oct-mult 0.25))
    (at (nome (+ 2 beat)) (gong :oct-mult 0.3))
    (at (nome (+ 3.5 beat)) (gong :oct-mult 0.4))
    (at (nome (+ 3.75 beat)) (gong :oct-mult 0.1))))

(defn play-perc [nome]
  (let [beat (+ (nome) 0.6)]
    (at (nome (+ 1 beat)) (pitch-perc :cf 2000 :amp 4.0))
    (at (nome (+ 1.125 beat)) (pitch-perc :cf 2100 :amp 3.0))
    (at (nome (+ 1.25 beat)) (pitch-perc :cf 2300 :amp 3.0))
    (at (nome (+ 1.375 beat)) (pitch-perc :cf 1900 :amp 4.0))
    (at (nome (+ 1.75 beat)) (pitch-perc :cf 2700 :amp 6.0))
    (at (nome (+ 2.1 beat)) (pitch-perc :cf 1300 :amp 6.0))))

(defn section [nome]
  (play-gong nome)
  (play-perc nome))

(def nome (metronome 44))
(section nome)

(pitch-perc :cf 100 :amp 100.0)  ; Timpani?
(pitch-perc :cf 500 :amp 8.0)
(pitch-perc :cf 1000 :amp 8.0)
(pitch-perc :cf 2000 :amp 8.0)
