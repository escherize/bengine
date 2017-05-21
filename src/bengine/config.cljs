(ns bengine.config
  (:require [macchiato.env :as config]
            [mount.core :refer [defstate]]))

(defstate env :start
  (merge
   {:posts-dir "_posts"
    :output-dir "blog"}
   (config/env)))

(defn posts-dir [] (:posts-dir @env))
(defn output-dir [] (:output-dir @env))
