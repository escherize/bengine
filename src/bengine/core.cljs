(ns bengine.core
  (:require [macchiato.fs :as fs]
            [bengine.config :refer [posts-dir output-dir]]
            [bengine.files :as bf]
            [bengine.tags]
            [clojure.string :as str]))

(enable-console-print!)


(set! *print-fn*
      (fn [& args]
        (let [print-args (remove #(re-matches #"^WARNING.*" %) args)]
          (.apply (.-log js/console) js/console (into-array print-args)))))

(defn super-print [s]
  (let [margin 4
        length (+ (* 2 margin) (count s))]
    (println (str/join "" (repeat length "=")))
    (println (str "||  " s "  ||"))
    (println (str/join "" (repeat length "=")))
    (println "")))

(defn main []
  (super-print "Starting a compilation!")
  (bf/compile-blog (posts-dir) (output-dir))
  (super-print "           Success!           "))


;; to trigger blog re-compiling, just re-save this file
 (->> (main) time)
