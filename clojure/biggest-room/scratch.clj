(ns user
  (:require [com.stuartsierra.component :as component]))

(defrecord DEMO [name]
  component/Lifecycle

  (start [component]
    (println "DEMO/start:" name)
    component)

  (stop [component]
    (println "DEMO/stop:" name)
    component))

(defn new-demo
  ;; Optional "constructor"
  [name]
  (map->DEMO {:name name}))

(defrecord PAIR [demo-1 demo-2]
  component/Lifecycle

  (start [component]
    (println "PAIR/start:" (:name demo-1) (:name demo-2))
    component)

  (stop [component]
    (println "PAIR/stop:" (:name demo-1) (:name demo-2))
    component)
  )

(defn system []
  (component/system-map
   :demo-1 (new-demo "ONE")
   :demo-2 (new-demo "TWO")
   :pair (component/using
          (map->PAIR {})
          [:demo-1 :demo-2])))

(def s (system))
