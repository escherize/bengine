(ns bengine.files
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [macchiato.fs :as fs]
            [cljs.nodejs :as node]
            [hiccups.runtime :as hiccupsrt]
            [cljs.repl :as repl]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            [clojure.string :as str]
            [bengine.config :as c]
            [bengine.tags :as my]))

(load-file "_tags/tags.cljs")

(def ^:private fs (node/require "fs"))

(defn- eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result]
          (println "got result: " (pr-str result))
          (:value result ::no-value))))

(defn- posts [path]
  {:pre [(fs/exists? path)]}
  (let [edn-file? #(str/ends-with? % ".edn")
        normalize-path #(str path "/" %)]
    (->> path
         fs/read-dir-sync
         (filter edn-file?)
         (map normalize-path)
         vec)))

(assert (= (posts (c/posts-dir))
           ["_posts/post_one.edn" "_posts/post_two.edn"]))

(defn- process-post [file-path]
  (println "reading -> " file-path)
  (let [process-hiccup (comp eval-str
                             ;; ;; evil stuff ------v  -------v
                              #(str/replace % "my/" "bengine.tags/")
                             fs/slurp)]
    {:content (process-hiccup file-path)
     :file-path file-path
     :last-modified (:birthtime (fs/stat file-path))}))

(assert (= (process-post "_posts/post_one.edn")
           {:content [:article "Hello World"],
            :file-path "_posts/post_one.edn",
            :last-modified #inst "2017-05-21T06:32:22.000-00:00"}))

(assert (= (process-post "_posts/post_two.edn")
           {:content [:article [:h1 "Hello World"] "Title works."],
            :file-path "_posts/post_two.edn",
            :last-modified #inst "2017-05-21T06:32:56.000-00:00"}))

(defn- process-posts [path]
  (->> (posts path)
       (mapv process-post)
       (sort-by (comp :file-info :ctime))))

(defn- get-file-name [file-path]
  (last (last (re-seq #"/(.[^/|*].+)" file-path))))

(defn- write-post [out-dir {:keys [content file-path last-modified]}]
  (println "writing -> " file-path)
  (let [output-file-name
        (-> (get-file-name file-path)
            (str/replace "edn" "html"))]
    (fs/spit (str out-dir "/" output-file-name)
             (html (my/template content)))))

(defn write-posts [posts-dir output-dir]
  (doseq [post-info (process-posts posts-dir)]
    (write-post output-dir post-info)))

(write-posts (c/posts-dir) (c/output-dir))

(defn- li-link [href title]
  [:li [:a {:href (str/replace href "edn" "html")} (str title " #" (rand))]])

(defn write-home [posts-dir output-dir]
  (let [out-file (str output-dir "/index.html")
        infos (process-posts posts-dir)
        hrefs (mapv (comp get-file-name :file-path) infos)
        out-str (html
                 [:h1 "Blog title #" (rand)]
                 (->> hrefs
                      (map #(li-link % "title"))
                      (into [:ul])))]
    (println "writing -> " out-file)
    (fs/spit out-file out-str)))
