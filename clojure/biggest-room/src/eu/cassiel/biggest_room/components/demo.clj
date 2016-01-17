(ns eu.cassiel.biggest-room.components.demo
  (:require [com.stuartsierra.component :as component]))

(defrecord DEMO [name running]
  component/Lifecycle

  (start [this]
    (if running
      (do
        (println "(DEMO/start)")
        this)
      (do
        (println "DEMO/start:" name)
        (assoc this :running true))))

  (stop [this]
    (if running
      (do
        (println "DEMO/stop:" name)
        (assoc this :running false))
      (do
        (println "(DEMO/stop)")
        this))))
