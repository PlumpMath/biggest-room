(ns user
  (:require [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            (eu.cassiel.biggest-room  [storage :as storage])
            (eu.cassiel.biggest-room.components [feeder :as feeder])))

;; ---

(count (deref feeder/A))

(-> (p/load-from (io/resource "test.properties"))
    (p/properties->map true))


(-> (io/resource "config.yaml")
    slurp
    yaml/parse-string)

(def *foo* 2)

;; ---

(reset)

user/S



(let [now (System/currentTimeMillis)]
  (map #(when-let [t (get-in % [:timestamp_ms])] (- now (Long/parseLong t)))
       (storage/read-store (:config S))))

(first (storage/read-store (:config S)))

(storage/write-store (:config S) (range 10))

feeder/A

(stop)

(-> (io/resource "config.yaml") (.toURI) (java.io.File.) (.getParent))






(instance? java.util.Map {})

(System/currentTimeMillis)
