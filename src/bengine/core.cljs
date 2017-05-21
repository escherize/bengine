(ns bengine.core
  (:require [macchiato.fs :as fs]
            [bengine.config :refer [posts-dir output-dir]]
            [bengine.files :as bf]
            [bengine.tags]
            [clojure.string :as str]))

(defn super-print [s]
  (let [margin 4
        length (+ (* 2 margin) (count s))]
    (js/console.log (str/join "" (repeat length "=")))
    (js/console.log (str "||  " s "  ||"))
    (js/console.log (str/join "" (repeat length "=")))
    (js/console.log "")))

(defn main []
  (super-print "Starting a compilation!")
  (bf/compile-blog (posts-dir) (output-dir))
  (super-print "           Success!           "))

;; to trigger blog re-compiling, just re-save this file
#_ (->> (main) time)
