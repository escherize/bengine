(ns bengine.core
  (:require [macchiato.fs :as fs]
            [bengine.config :refer [posts-dir out-dir]]
            [bengine.files :as bf]
            [bengine.tags]
            [clojure.string :as str]))

(enable-console-print!)

(defn super-print
  ([s] (super-print s ";"))
  ([s c]
   (let [margin 4
         length (+ (* 2 margin) (count s))]
     (println (str/join "" (repeat length c)))
     (println (str c c "  " s "  " c c))
     (println (str/join "" (repeat length c)))
     (println ""))))

(defn main []
  (super-print "Compiling your blog!" (rand-nth ["X" "!" ";" "*" "o" "O" "." "\""]))
  (bf/compile-blog (posts-dir) (out-dir))
  (super-print "                  Success!                  " ";"))

;; to trigger blog re-compiling, just re-save this file
;; (->> (main) time)
