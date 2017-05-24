(ns bengine.tags
  (:require [clojure.string :as str]))

(defn html-page [form]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]]
   [:body form]])

;; post infos looks like this:
;; [{:href "lemon.html"
;;  :title "How I learned to do a thing."}]
(defn home-template [post-infos]
  [:article
   [:h1 "My Blog"]
   [:p "Welcome or some-such."]
   [:ul
    (for [{:keys [link title] :as info} post-infos]
      [:li [:a {:href link} title]])]])

(defn post-template [& forms]
  (into [:article] forms))

(defn title [s] [:h1 s])

(defn p [& xs]
  [:p (str/join \newline xs)])
