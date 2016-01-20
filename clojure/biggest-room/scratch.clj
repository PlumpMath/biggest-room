(ns user
  (:require [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io]
            [clj-yaml.core :as yaml]))

;; ---

(-> (p/load-from (io/resource "test.properties"))
    (p/properties->map true))


(-> (io/resource "config.yaml")
    slurp
    yaml/parse-string)

;; ---

(reset)

(stop)
