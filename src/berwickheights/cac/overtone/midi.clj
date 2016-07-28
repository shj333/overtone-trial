(ns berwickheights.cac.overtone.midi
  (:require [overtone.core :as ot]))

(defonce ^:private last-handler-id (atom 0))
(defonce ^:private this-ns (str *ns*))
(defn- get-handler-name [id] (keyword this-ns (str "midi-event-hdlr-" id)))

(defn on-event
  "Sets up the given handler to respond to all MIDI note-on events, each of which passes the MIDI event
  to the handler. Returns an id that can be used to turn off the handler with a call to off-event.

  Example:

  (defn my-hdlr [e] (println \"The event is \" e))
  (def my-id (on-event my-hdlr))
  (off-event my-id)"
  [handler]
  (let [id (swap! last-handler-id inc)]
    (ot/on-event [:midi :note-on] handler (get-handler-name id))
    id))

(defn off-event
  "Removes the MIDI event handler with the given ID originally returned from a call to on-event.

  Example:

  (defn my-hdlr [e] (println \"The event is \" e))
  (def my-id (on-event my-hdlr))
  (off-event my-id)"
  [id]
  (ot/remove-event-handler (get-handler-name id)))

(defn midi-debug
  "Prints MIDI events with the given keys for each note-on event. Toggle the debugging on with keyword :on.
  Anything else turns debugging off.

  Example:

  (midi-debug :on :note :velocity)
  (midi-debug :off)"
  [toggle & keys]
  (if (= toggle :on)
    (ot/on-event [:midi :note-on] #(println (select-keys %1 keys)) ::midi-debug)
    (ot/remove-event-handler ::midi-debug)))
