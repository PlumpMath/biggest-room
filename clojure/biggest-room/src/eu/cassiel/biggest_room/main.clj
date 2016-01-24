(ns eu.cassiel.biggest-room.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [eu.cassiel.biggest-room.components.demo :as demo]
            [eu.cassiel.biggest-room.components.config :as config]
            [eu.cassiel.biggest-room.components.storage :as storage]
            [eu.cassiel.biggest-room.components.feeder :as feeder]))

(defn system []
  (component/system-map
   :config (component/using
            (config/map->CONFIG {})
            [])
   :storage (component/using
             (storage/map->STORAGE {})
             [:config])
   :feeder (component/using
            (feeder/map->FEEDER {})
            [:config :storage])
   :app (component/using
         (demo/map->DEMO {:name "Hello"})
         []))  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
