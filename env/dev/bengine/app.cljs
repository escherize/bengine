 (ns ^:figwheel-always bengine.app
  (:require
    [bengine.core :as core]
    [cljs.nodejs :as node]
    [mount.core :as mount]))

(enable-console-print!)

(mount/in-cljc-mode)

(cljs.nodejs/enable-util-print!)

(.on js/process "uncaughtException" #(js/console.error %))

(set! *main-cli-fn* core/main)
