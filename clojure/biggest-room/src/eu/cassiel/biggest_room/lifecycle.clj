(ns eu.cassiel.biggest-room.lifecycle)

(defn starting [component & {:keys [on action]}]
  (if on
    (do
      (println "   already running: " component)
      component)
    (do
      (println "              + >>: " component)
      (action))))

(defn stopping [component & {:keys [on action]}]
  (if on
    (do
      (println "              << -: " component)
      (action))
    (do
      (println "   already stopped: " component)
      component)))
