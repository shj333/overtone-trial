(ns berwickheights.experiments.core-logic
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))

(run* [q]
      (membero q [1 2 3])
      (membero q [2 3 4]))

(run* [q1 q2]
      (membero q1 [1 2 3])
      (membero q2 [2 3 4]))

(run* [q]
      (resto q [1 2 3]))


(defne moveo [before action after]
       ([[:middle :onbox :middle :hasnot]
         :grasp
         [:middle :onbox :middle :has]])
       ([[pos :onfloor pos has]
         :climb
         [pos :onbox pos has]])
       ([[pos1 :onfloor pos1 has]
         :push
         [pos2 :onfloor pos2 has]])
       ([[pos1 :onfloor box has]
         :walk
         [pos2 :onfloor box has]]))

(defne cangeto [state out]
       ([[_ _ _ :has] true])
       ([_ _] (fresh [action next]
                     (moveo state action next)
                     (cangeto next out))))

(run 1 [q]
     (cangeto [:atdoor :onfloor :atwindow :hasnot] q))


