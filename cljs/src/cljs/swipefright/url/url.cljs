(ns swipefright.url
  (:require [clojure.spec.alpha :as s]))

(def translation-table
  {"A" 0 "B" 1 "C" 2 "D" 3 "E" 4 "F" 5 "G" 6 "H" 7 "I" 8 "J" 9
   "K" 10 "L" 11 "M" 12 "N" 13 "O" 14 "P" 15 "Q" 16 "R" 17 "S" 18 "T" 19
   "U" 20 "V" 21 "W" 22 "X" 23 "Y" 24 "Z" 25 "a" 26 "b" 27 "c" 28 "d" 29
   "e" 30 "f" 31 "g" 32 "h" 33 "i" 34 "j" 35 "k" 36 "l" 37 "m" 38 "n" 39
   "o" 40 "p" 41 "q" 42 "r" 43 "s" 44 "t" 45 "u" 46 "v" 47 "w" 48 "x" 49
   "y" 50 "z" 51})

;; Least signifiant values starting from the left
(defn decode [lookup-table string base]
  (reduce 
    + 
    (map 
      #(* (lookup-table %1) %2) 
      string 
      (iterate (partial * base) 1)))) 

(defn decode52 [string]
  (decode translation-table string 52))

(defn convert-to-base [n b]
  (loop [r ()
         n n]
    (if (< n b)
      (conj r n)
      (recur (conj r (mod n b)) (quot n b)))))

(s/def ::natural (s/and int? pos?))

(s/fdef encode52
    :args (s/cat :n ::natural)
    :ret string?)

(defn encode52 [n]
  {:pre [(s/valid? ::natural n)]
   :post [(s/valid? string? %)]}
  (->> (convert-to-base n 52)
        (map #(get (clojure.set.map-invert translation-table) %))
        (reduce #(str %1 %2))))
