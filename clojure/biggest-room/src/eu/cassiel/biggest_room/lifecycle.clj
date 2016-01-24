(ns eu.cassiel.biggest-room.lifecycle)

(def pad (partial format "%20s: "))

(defn starting [component & {:keys [on action]}]
  (if on
    (do
      (println (pad "already running") (class component))
      component)
    (do
      (println (pad "+ >>") (class component))
      (action))))

(defn stopping [component & {:keys [on action]}]
  (if on
    (do
      (println (pad "<< -") (class component))
      (action))
    (do
      (println (pad "already stopped") (class component))
      component)))
