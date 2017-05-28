(ns bengine.files
  (:require [macchiato.fs :as fs]
            [cljs.nodejs :as node]
            [hiccups.runtime :as hiccupsrt]
            [cljs.repl :as repl]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            [clojure.string :as str]
            [bengine.config :as c]
            [bengine.tags :as my] ;; <-- load this for evalling templates
            )
  (:require-macros [hiccups.core :as hiccups]))

(defn- get-file-name [file-path]
  (last (last (re-seq #"/(.[^/|*].+)" file-path))))

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

(defn ->title [file-path]
  (as-> file-path $
    (get-file-name $)
    (str/replace $ ".edn" "")
    (str/replace $ "_" " ")
    (str/split $ " ")
    (map str/capitalize $)
    (str/join " " $)))

(defn- process-post [file-path]
  (println "processing: " file-path)
  (println "reading --> " file-path)
  (let [process-hiccup (comp eval-str
                             ;; ;; evil stuff ------v  -------v
                             #(str/replace % "my/" "bengine.tags/")
                             fs/slurp)]
    {:content (process-hiccup file-path)
     :file-path file-path
     :here (-> file-path
               get-file-name
               (str/replace "edn" "html"))
     :title (->title file-path)
     :creation-time (:birthtime (fs/stat file-path))}))

(defn add-next-prev [posts]
  (vec
    (map-indexed
      (fn [idx p]
        (let [next-file (:here (get posts (inc idx)))
              prev-file (:here (get posts (dec idx)))]
          (assoc p :next next-file :prev prev-file)))
      posts)))

(defn- process-posts [path]
  (->> (posts path)
       (mapv process-post)
       (sort-by :file-path)
       vec
       add-next-prev))

(defn- write-post [out-dir in-dir {:keys [content file-path here title creation-time prev next]}]
  (println "writing post -> " file-path)
  (fs/spit (str out-dir "/" here)
           (hiccups/html
             (my/post content {:title title
                               :creation-time creation-time
                               :here here
                               :next next
                               :prev prev
                               :up "index.html"}))))


(defn- write-posts [processed-posts out-dir in-dir]
  (doseq [post-info processed-posts]
    (write-post out-dir in-dir post-info)))

(defn- write-index [processed-posts out-dir]
  (let [out-str (-> processed-posts my/index hiccups/html)
        out-file (str out-dir "/index.html")]
    (println "writing index -> " out-file)
    (fs/spit out-file out-str)))

(defn compile-blog [in-dir out-dir]
  (println "in-dir: " in-dir)
  (let [processed-posts (process-posts in-dir)]
    (println "writing posts...")
    (write-posts processed-posts out-dir in-dir)
    (println "writing index...")
    (write-index processed-posts out-dir)))

(bengine.core/main)
