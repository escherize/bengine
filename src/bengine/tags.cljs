(ns bengine.tags
  (:require [clojure.string :as str]))

(def moment (js/require "moment"))

;; common wrapper for all pages
(defn html [& forms]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet"
            :href "https://cdnjs.cloudflare.com/ajax/libs/tufte-css/1.1/tufte.min.css"}]]
   [:body forms]])

;; wrapper for the index page (home page)
(defn index [post-infos]
  (html
    [:article
     [:h1 "My Blog"]
     [:p "Welcome or some-such."]
     (into
       [:ul]
       ;; Example post info:
       (for [{:keys [here title creation-time]}
             post-infos]
         [:li
          [:a {:href here} title]
          [:span " - (posted on: " (.format (moment creation-time) "LL") ")"]]))]))

;; wrapper common to all posts
(defn post [content {:keys [title creation-time here next prev up]}]
  (html
    [:h1 [:a {:href here} title]]
    [:span "Posted on: " creation-time]
    content
    (when prev
      [:a {:href prev} "<--"])
    [:span {:style "margin: auto 10px;"}
     [:a {:href up} "back"]]
    (when next
      [:a {:href next} "-->"])))

;; user's own tags:
(defn title [s] [:h1 s])

(defn p [& xs]
  [:p (str/join \newline xs)])
