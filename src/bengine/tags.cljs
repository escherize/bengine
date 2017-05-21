(ns bengine.tags
  (:require [clojure.string :as str]))

(defn- li-link [href title]
  [:li [:a {:href (str/replace href ".edn" ".html")} title]])

(defn home-template [hrefs titles]
  [:article
   [:h1 "Blog title"]
   [:p "Welcome or some-such."]
   (into [:ul]
         (map #(li-link % %2) hrefs titles))])

(defn post-template [& forms]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]]
   (into [:body] forms)])

(defn title [s] [:h1 s])

(defn p [& xs] [:p (str/join \newline xs)])
