(ns berwickheights.cac.pitch-sets
  (:require [berwickheights.cac.overtone.midi :as midi]))

(defonce ^:private pc-sets* (atom {}))

(defn def-pc-set
  "Define a pitch class set using MIDI keyboard. New pc set has the given name which can be used
  to retrieve it later using the function get-pc-set. Pitches are added to the pc set in order
  until a MIDI note of 36 or lower is pressed."
  [set-name]
  (swap! pc-sets* assoc set-name [])
  (declare hdlr-id)
  (letfn [(stop-get-pcs []
            (midi/off-event hdlr-id)
            (println (str "Pitch class set is now available using (get-pc-set " set-name ")")))

          (set-pc [midi-note]
            (let [pc (rem midi-note 12)]
              (swap! pc-sets* assoc set-name (conj (set-name @pc-sets*) pc))
              (println "Set PC " pc)))

          (handle-note [e]
            (let [midi-note (:note e)]
              (if (<= midi-note 36)
                (stop-get-pcs)
                (set-pc midi-note))))]
    (def hdlr-id (midi/on-event handle-note))
    (println "Waiting for midi keyboard events -- midi 36 or lower to stop")))

(defn get-pc-set
  "Retrieves a pc set previously defined by call to def-pc-set. Returns nil if a pc set with the
  given name was not preivously defined."
  [set-name] (set-name @pc-sets*))

(defn get-all-names
  "Returns all previously define pc set names."
  [] (keys @pc-sets*))

(defn clear-all
  "Clears all previously defined pc sets."
  [] (reset! pc-sets* {}))

