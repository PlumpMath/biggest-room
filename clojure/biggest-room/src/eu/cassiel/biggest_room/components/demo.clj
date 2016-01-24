(ns eu.cassiel.biggest-room.components.demo
  (:require [com.stuartsierra.component :as component]
            [eu.cassiel.biggest-room.lifecycle :refer [starting stopping]]))

(defrecord DEMO [name running]
  component/Lifecycle

  (start [this]
    (starting this :on running :action #(assoc this :running true)))

  (stop [this]
    (stopping this :on running :action #(assoc this :running nil))))
