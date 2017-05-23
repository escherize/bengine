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
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result]
          #_(js/console.log "got result: " (pr-str result))
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

(defn- process-post [file-path]
  (js/console.log "reading -> " file-path)
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
  (js/console.log "writing -> " file-path)
  (let [output-file-name
        (-> (get-file-name file-path)
            (str/replace "edn" "html"))]
    (fs/spit (str out-dir "/" output-file-name)
             (html (my/post-template content)))))

(defn- write-posts [processed-posts output-dir]
  (doseq [post-info processed-posts]
    (write-post output-dir post-info)))

(defn- write-index [processed-posts output-dir]
  (let [hrefs (mapv (comp get-file-name :file-path)
                    processed-posts)
        titles (mapv (comp str/capitalize get-file-name :file-path)
                     processed-posts)
        out-str (html (my/index-template hrefs titles))
        out-file (str output-dir "/index.html")]
    (js/console.log "writing -> " out-file)
    (fs/spit out-file out-str)))

(defn compile-blog [posts-dir output-dir]
  (js/console.log "posts-dir" posts-dir)
  (let [processed-posts (process-posts posts-dir)]
    (js/console.log "writing posts...")
    (write-posts processed-posts output-dir)
    (js/console.log "writing index...")
    (write-index processed-posts output-dir)))
