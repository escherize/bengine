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
            [bengine.tags :as my] ;; <-- load this for evalling templates
            ))

(def ^:private fs (node/require "fs"))

(defn- eval-str [s]
  (eval (empty-state)
        (read-string
          (str "(do (enable-console-print!)" s ")"))
        {:eval js-eval
         :source-map true
         :context :expr}
        (fn [result]
          (println "got result: " (pr-str result))
          (:value result ::no-value))))

(defn- posts [path]
  {:pre [(fs/exists? path)]}
  (let [edn-file? #(str/ends-with? % ".edn")
        normalize-path #(str path "/" %)]
    (->> path fs/read-dir-sync (filter edn-file?) (map normalize-path) vec)))

(defn- process-post [file-path]
  (println "")
  (println "reading -> " file-path)
  (let [process-hiccup (comp eval-str
                             ;; ;; evil stuff ------v  -------v
                             #(str/replace % "my/" "bengine.tags/")
                             fs/slurp)]
    {:content (process-hiccup file-path)
     :file-path file-path
     :last-modified (:birthtime (fs/stat file-path))}))

(defn- process-posts [path]
  (->> (posts path)
       (mapv process-post)
       (sort-by :file-path)))

(defn- get-file-name [file-path]
  (last (last (re-seq #"/(.[^/|*].+)" file-path))))

(defn- write-post [out-dir {:keys [content file-path last-modified]}]
  (println "writing -> " file-path)
  (let [out-html-file (-> file-path get-file-name (str/replace "edn" "html"))]
    (fs/spit (str out-dir "/" out-html-file)
             (html (my/post-template content)))))

(defn- write-posts [processed-posts output-dir]
  (doseq [post-info processed-posts]
    (write-post output-dir post-info)))

(defn- write-index [processed-posts output-dir]
  (let [post-infos (map (fn [pposts] {:href (comp get-file-name :file-path)
                                      :title (comp str/capitalize get-file-name :file-path)})
                        processed-posts)
        out-str (-> post-infos my/home-template html)
        out-file (str output-dir "/index.html")]
    (println "writing -> " out-file)
    (fs/spit out-file out-str)))

(defn compile-blog [posts-dir output-dir]
  (println "posts-dir" posts-dir)
  (let [processed-posts (process-posts posts-dir)]
    (println "writing posts...")
    (write-posts processed-posts output-dir)
    (println "writing index...")
    (write-index processed-posts output-dir)))
