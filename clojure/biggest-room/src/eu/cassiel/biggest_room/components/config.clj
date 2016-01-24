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
              :action #(let [res (io/resource "config.yaml")]
                         (assoc this
                                :entries (-> res
                                             slurp
                                             yaml/parse-string)
                                :my-file (-> res (.toURI) (java.io.File.))))))

  (stop [this]
    this))
