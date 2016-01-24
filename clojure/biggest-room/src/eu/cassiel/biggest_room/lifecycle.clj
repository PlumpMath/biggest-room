(ns eu.cassiel.biggest-room.lifecycle)

(defn starting [component & {:keys [on action]}]
  (if on
    (do
      (println "   already running: " (class component))
      component)
    (do
      (println "              + >>: " (class component))
      (action))))

(defn stopping [component & {:keys [on action]}]
  (if on
    (do
      (println "              << -: " (class component))
      (action))
    (do
      (println "   already stopped: " (class component))
      component)))
