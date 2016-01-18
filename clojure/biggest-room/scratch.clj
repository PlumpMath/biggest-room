(ns user
  (:require [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io]))

;; ---

(-> (p/load-from (io/resource "test.properties"))
    (p/properties->map true))

;; ---

(reset)

(stop)
