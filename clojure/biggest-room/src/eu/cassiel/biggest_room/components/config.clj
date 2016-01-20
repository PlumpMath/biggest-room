(ns eu.cassiel.biggest-room.components.config
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clj-yaml.core :as yaml]))

(defrecord CONFIG [entries]
  component/Lifecycle

  (start [this]
    (assoc this :entries (-> (io/resource "config.yaml")
                             slurp
                             yaml/parse-string)))

  (stop [this]
    (assoc this :entries nil)))
