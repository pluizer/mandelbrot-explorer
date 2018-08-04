(ns ^:figwheel-no-load brot.dev
  (:require
    [brot.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
