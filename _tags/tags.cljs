(ns bengine.tags
  (:require [clojure.string :as str]))

(defn template [& forms]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]]
   (into [:body] forms)])

(defn title [s] [:h1 s])

(defn p [& xs] [:p (str/join \newline xs)])
