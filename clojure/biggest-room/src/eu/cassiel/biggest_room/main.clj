(ns eu.cassiel.biggest-room.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [eu.cassiel.biggest-room.components.demo :as demo]))

(defn system []
  (component/system-map
   :app (component/using
         (demo/map->DEMO {:name "Hello"})
         []))  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
