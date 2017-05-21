(ns bengine.core
  (:require [macchiato.fs :as fs]
            [bengine.config :refer [posts-dir output-dir]]
            [bengine.files :as bf]
            [bengine.tags]))

(defn main [& args]
  (println "main got posts-dir: " (posts-dir))
  (println "main got output-dir: " (output-dir))
  (bf/write-posts (posts-dir) (output-dir))
  (println "Your blog is ready at: ./" (output-dir))
  (bf/write-home (posts-dir) (output-dir))
  "Success")
