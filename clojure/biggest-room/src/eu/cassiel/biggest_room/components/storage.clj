(ns eu.cassiel.biggest-room.components.storage
  "Persistence for the Tweet store."
  (:require [com.stuartsierra.component :as component]
            [eu.cassiel.biggest-room.lifecycle :refer [starting stopping]]
            [taoensso.nippy :as nippy]
            [clojure.java.io :as io])
  (:import [java.io File DataInputStream DataOutputStream]))

(defrecord STORAGE [config file data*]
  component/Lifecycle
  (start [this]
    (starting this
              :on file

              :action
              #(let [f (-> (get-in config [:my-file])
                           (.getParent)
                           (File. (get-in config [:entries :storage])))
                     data* (atom (if (.exists f)
                                   (with-open [r (io/input-stream f)]
                                     (nippy/thaw-from-in! (DataInputStream. r)))
                                   nil))]
                 (assoc this
                        :file f
                        :data* data*))))

  (stop [this]
    (stopping this
              :on file
              :action #(assoc this :file nil))))

(defn swap-in! [storage f]
  (swap! (:data* storage) f))

;; TODO We're filtering everything on each pass, for now, until we're clear about our
;; data store.

(defn put! [storage x]
  (swap-in! storage (fn [s]
                      (let [now (System/currentTimeMillis)]
                        (as-> (conj s x) X
                          (filter #(= "en" (:lang %)) X)
                          (filter :timestamp_ms X)
                          (filter #(< (- now (Long/parseLong (:timestamp_ms %))) 120000) X))))))

(defn flush! [storage]
  (with-open [w (io/output-stream (:file storage))]
    (nippy/freeze-to-out! (DataOutputStream. w)
                          (deref (:data* storage)))))
