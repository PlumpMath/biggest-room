(ns eu.cassiel.biggest-room.components.config
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [eu.cassiel.biggest-room.lifecycle :refer [starting stopping]]))

(defrecord CONFIG [entries]
  component/Lifecycle

  (start [this]
    (starting this
              :on entries
              :action #(assoc this :entries (-> (io/resource "config.yaml")
                                                slurp
                                                yaml/parse-string))))

  (stop [this]
    (stopping this
              :on entries
              :action #(assoc this :entries nil))))
