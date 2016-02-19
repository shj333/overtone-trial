(ns berwickheights.cac.notation.lilypond
  (:require [clojure.java.shell :as sh]
            [clojure.string :as s]))

(defonce ^:private lily-pitches ["c" "df" "d" "ef" "e" "f" "gf" "g" "af" "a" "bf" "b"])
(defonce ^:private lily-out-dir "lily/")
(defonce ^:private default-paper-size "junior-legal")
(defonce ^:private lily-base-str "
\\version \"2.18.2\"
\\language english

\\paper {
  #(set-paper-size \"PAPER_SIZE\")
}

<<
  \\new Staff
  {
    \\clef treble
    TREBLE_NOTES
  }
  \\new Staff
  {
    \\clef bass
    BASS_NOTES
  }
>>")

(defn- lily-path [filename] (str lily-out-dir (name filename)))

(defn- gen-pitch [pc octave-map]
  (let [octave (octave-map pc)]
    (if (< octave 4)
      (apply str (lily-pitches pc) (repeat (- 3 octave) ","))
      (apply str (lily-pitches pc) (repeat (- octave 3) "'")))))

(defn- gen-chord [pitches]
  (if (empty? pitches)
    "r1"
    (str "<" (s/join " " pitches) ">1")))

(defn- notate-set
  "Creates a PDF of the pc set with musical notation derived using Lilypond. Opens the PDF in a separate window."
  [pc-set octave-map]
  (let [grouping (group-by #(< (octave-map %) 4) pc-set)
        bass-pitches (map gen-pitch (grouping true) (repeat octave-map))
        treble-pitches (map gen-pitch (grouping false) (repeat octave-map))]
    [(gen-chord bass-pitches) (gen-chord treble-pitches)]))

(defn notate
  "Creates a PDF of the pc sets/octave-maps with musical notation derived using Lilypond. Returns the path to the
  generated file."
  ([set-name pc-sets octave-maps] (notate set-name pc-sets octave-maps default-paper-size))
  ([set-name pc-sets octave-maps paper-size]
   (let [pitches (map notate-set pc-sets octave-maps)
         lily-str (-> (s/replace lily-base-str "BASS_NOTES" (s/join " " (map first pitches)))
                      (s/replace "TREBLE_NOTES" (s/join " " (map second pitches)))
                      (s/replace "PAPER_SIZE" paper-size))
         file-path (lily-path set-name)
         lily-out (sh/sh "lilypond" (str "-o" file-path) "-" :in lily-str)]
     (if (= 0 (:exit lily-out))
       file-path
       (and (println (:err lily-out)) false)))))

(defn notate-and-open
  "Creates a PDF of the pc sets/octave-maps with musical notation derived using Lilypond.
  Opens the PDF in a separate window."
  ([set-name pc-sets octave-maps] (notate-and-open set-name pc-sets octave-maps default-paper-size))
  ([set-name pc-sets octave-maps paper-size]
   (let [file-path (notate set-name pc-sets octave-maps paper-size)]
     (if file-path
       (and (sh/sh "open" (str file-path ".pdf")) true)
       false))))
