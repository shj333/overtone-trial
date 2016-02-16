(ns berwickheights.cac.overtone.midi
  (:require [overtone.core :as ot]))

(defonce ^:private last-handler-id* (atom 0))
(defonce ^:private this-ns (str *ns*))
(defn- get-handler-name [id] (keyword this-ns (str "midi-event-hdlr-" id)))

(defn on-event
  [handler data]
  (let [id (swap! last-handler-id* inc)]
    (ot/on-event [:midi :note-on] #(handler %1 id data) (get-handler-name id))
    id))

(defn off-event
  [id]
  (ot/remove-event-handler (get-handler-name id)))

(defn midi-debug
  [toggle & keys]
  (if (= toggle :on)
    (ot/on-event [:midi :note-on] #(println (select-keys %1 keys)) ::midi-debug)
    (ot/remove-event-handler ::midi-debug)))
