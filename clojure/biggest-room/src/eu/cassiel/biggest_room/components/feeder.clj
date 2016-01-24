(ns eu.cassiel.biggest-room.components.feeder
  "Feed from Twitter. Periodically turn on a stream for a few seconds."
  (:require [com.stuartsierra.component :as component]
            [eu.cassiel.biggest-room.lifecycle :refer [starting stopping]]
            [eu.cassiel.biggest-room.components [storage :as storage]]
            [twitter.oauth :as oauth]
            [twitter.callbacks.handlers :as h]
            [twitter.api.streaming :as streaming]
            [chime :refer [chime-at chime-ch]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.core.async :as async :refer [<! go-loop]]
            [clojure.data.json :as json])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback
                                        SyncSingleCallback)))

;; FEEDER_PASS is a component, but one that we start and stop repeatedly
;; to take a feed and update our state atom.

(defrecord FEEDER_PASS [config storage credentials response]
  component/Lifecycle
  (start [this]
    (starting this
              :on response

              :action
              #(do
                (let [twitter (get-in config [:entries :twitter])
                      credentials (oauth/make-oauth-creds (:app-consumer-key twitter)
                                                          (:app-consumer-secret twitter)
                                                          (:user-access-token twitter)
                                                          (:user-access-token-secret twitter))

                      hack-body-part (fn [response baos]
                                       (try
                                         (do
                                           ;; LOTS of defensive coding here:
                                           (when-let [x (-> baos
                                                            str
                                                            json/read-json)]
                                             (when (instance? java.util.Map x)
                                               (storage/put! storage x))))
                                         (catch Exception exn
                                           nil)))

                      ;; For now: start immediately.
                      response (streaming/statuses-sample
                                :oauth-creds credentials
                                :callbacks (AsyncStreamingCallback.
                                            hack-body-part

                                            (constantly nil)
                                            #_(comp println h/response-return-everything)

                                            (constantly nil)
                                            #_ h/exception-print))]
                  (assoc this
                         :credentials credentials
                         :response response)))))

  (stop [this]
    (stopping this
              :on response
              :action #(do
                         ((:cancel (meta response)))
                         (assoc this
                                :credentials nil
                                :response nil)))))

(defrecord FEEDER [config storage subsystem* ch]
  component/Lifecycle
  (start [this]
    (starting this
              :on subsystem*

              :action
              #(do
                 (let [ch (chime-ch (periodic-seq (t/now)
                                                  (-> 60 t/seconds)))
                       subsystem* (atom (component/system-map
                                         :feeder-pass (component/using
                                                       (map->FEEDER_PASS {:config config
                                                                          :storage storage})
                                                       [])))]
                   (go-loop [] (when-let [t (<! ch)]
                                 (println "Chiming at" t)
                                 (swap! subsystem* component/start)
                                 (future (Thread/sleep 5000)
                                         (swap! subsystem* component/stop)
                                         (storage/flush! storage))
                                 (recur)))

                   (assoc this
                          :subsystem* subsystem*
                          :ch ch)))))

  (stop [this]
    (stopping this
              :on subsystem*
              :action #(do
                         (async/close! ch)
                         (component/stop @subsystem*)
                         (assoc this
                                :ch nil
                                :subsystem* nil)))))
