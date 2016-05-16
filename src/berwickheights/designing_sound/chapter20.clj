(ns berwickheights.designing-sound.chapter20
  (:use [overtone.core]))


; Designing Sound, Chapter 20: Amp Mod

(definst amod [car-freq 320 mod-freq 440]
         (let [mod (sin-osc mod-freq)]
           (* car-freq mod 0.1)))