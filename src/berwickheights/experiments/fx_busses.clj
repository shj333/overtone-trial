(ns berwickheights.experiments.fx-busses
  (:use [overtone.core]))

;
; Simple reverb test
;
(definst test-reverb []
         (let [signal (decay (impulse:ar 0.25 (lf-cub 1200 0)))
               signal2 (* (env-gen (env-perc :attack 0.01 :release 0.2 :level 1 :curve -4)) (sin-osc))
               mix 0.4
               room 0.7
               damp 0.2]
           (free-verb signal2 mix room damp)))
(demo (test-reverb))
(stop)


;
; Bus Test with reverb fx
;
(defsynth my-pass-thru [in-bus 0 out-bus 0]
          (out:ar out-bus (in:ar in-bus)))

(defsynth my-reverb [in-bus 0 out-bus 0 mix 0.5 room 0.7 damp 0.2]
          (out:ar out-bus (free-verb (in:ar in-bus) mix room damp)))

(defsynth my-g-reverb [in-bus 0 out-bus 0 room-size 5 rev-time 0.6 damping 0.62 input-bw 0.48 spread 15 dry-level 0.5 early-level 0.28 tail-level 0.22]
          (out:ar out-bus (g-verb (in:ar in-bus) room-size rev-time damping input-bw spread dry-level early-level tail-level)))

(defsynth my-boop [out-bus 0]
          (out:ar out-bus (* (env-gen (env-perc :attack 0.01 :release 0.2 :level 1 :curve -4) :action FREE) (sin-osc))))

(do
  (defonce fx-bus (audio-bus))
  (defonce pass-thru-bus (audio-bus))
  (defonce fx-main-grp (group "fx test main"))
  (defonce producer-grp (group "sound gen" :head fx-main-grp))
  (defonce fx-grp (group "fx" :after producer-grp)))
(pp-node-tree)

(def no-fx (my-pass-thru [:tail fx-grp] pass-thru-bus))
(def reverb-inst (my-reverb [:tail fx-grp] fx-bus))

(def boop-inst (my-boop [:tail producer-grp] pass-thru-bus))
(def boop-inst (my-boop [:tail producer-grp] fx-bus))
(stop)



;
; Stereo Bus Test
;
(defsynth my-reverb2 [in-bus 0 out-bus 0 mix 0.5 room 0.7 damp 0.2]
          (let [in (in:ar in-bus 2)]
            (out:ar out-bus (free-verb2 (nth in 0) (nth in 1) mix room damp))))

(defsynth my-boop2 [out-bus 0 pos 0.0]
          (let [sig (* (env-gen (env-perc :attack 0.01 :release 0.2 :level 1 :curve -4) :action FREE) (sin-osc))]
            (out:ar out-bus (pan2 sig pos 1.0))))

(do
  (defonce fx2-bus (audio-bus))
  (defonce fx-main2-grp (group "fx test main stereo"))
  (defonce producer2-grp (group "sound gen stereo" :head fx-main2-grp))
  (defonce fx2-grp (group "fx stereo" :after producer2-grp)))
(pp-node-tree)

(def reverb2-inst (my-reverb2 [:tail fx2-grp] fx2-bus))

(def boop2-inst (my-boop2 [:tail producer2-grp] fx2-bus -1))
(def boop2-inst (my-boop2 [:tail producer2-grp] fx2-bus 0))
(def boop2-inst (my-boop2 [:tail producer2-grp] fx2-bus 1))
(stop)

