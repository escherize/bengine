(ns bengine.config
  (:require [macchiato.env :as config]
            [mount.core :refer [defstate]]))

(defstate env :start
  (merge
   {:posts-dir "_posts"
    :out-dir "public"}
   (config/env)))

(defn posts-dir [] (:posts-dir @env))
(defn out-dir [] (:out-dir @env))
