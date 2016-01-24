(ns eu.cassiel.biggest-room.storage
  "Persistence for the Tweet store."
  (:require [taoensso.nippy :as nippy]
            [clojure.java.io :as io])
  (:import [java.io File DataInputStream DataOutputStream]))

(defn storage-file [config]
  (-> (get-in config [:my-file])
      (.getParent)
      (File. (get-in config [:entries :storage]))))

(defn read-store [config]
  (let [f (storage-file config)]
    (if (.exists f)
      (with-open [r (io/input-stream f)]
        (nippy/thaw-from-in! (DataInputStream. r)))
      nil)))

(defn write-store [config data]
  (with-open [w (io/output-stream (storage-file config))]
    (nippy/freeze-to-out! (DataOutputStream. w) data)))
