 (ns bengine.app
  (:require
    [bengine.core :as core]
    [cljs.nodejs]
    [mount.core :as mount]))

(enable-console-print!)

(mount/in-cljc-mode)

(cljs.nodejs/enable-util-print!)

(set! *main-cli-fn* core/main)
