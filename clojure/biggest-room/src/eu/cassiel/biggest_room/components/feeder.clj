(ns eu.cassiel.biggest-room.components.feeder
  "Feed from Twitter. Periodically turn on a stream for a few seconds."
  (:require [com.stuartsierra.component :as component]
            [eu.cassiel.biggest-room.lifecycle :refer [starting stopping]]
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

(def A (atom nil))

;; FEEDER_PASS is a component, but one that we start and stop repeatedly
;; to take a feed and update our state atom.

(defrecord FEEDER_PASS [config credentials response]
  component/Lifecycle

  (start [this]
    (starting this
              :on response

              :action
              #(do
                (reset! A nil)
                (let [twitter (get-in config [:entries :twitter])
                      credentials (oauth/make-oauth-creds (:app-consumer-key twitter)
                                                          (:app-consumer-secret twitter)
                                                          (:user-access-token twitter)
                                                          (:user-access-token-secret twitter))

                      hack-body-part (fn [response baos]
                                       (try
                                         (do
                                           (println "ping!")
                                           (let [text (-> baos
                                                          str
                                                          json/read-json
                                                          :text)]
                                             (swap! A conj text)))
                                         (catch Exception exn
                                           (swap! A conj (.getMessage exn)))))

                      ;; For now: start immediately.
                      response (streaming/statuses-sample
                                :oauth-creds credentials
                                :callbacks (AsyncStreamingCallback.
                                            hack-body-part
                                            (comp println h/response-return-everything)
                                            h/exception-print))]
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

(defrecord FEEDER [config subsystem ch]
  component/Lifecycle

  (start [this]
    (starting this
              :on subsystem

              :action
              #(do
                 (let [ch (chime-ch (periodic-seq (t/now)
                                                  (-> 60 t/seconds)))
                       subsystem (atom (component/system-map
                                        :feeder-pass (component/using
                                                      (map->FEEDER_PASS {:config config})
                                                      [])))]
                   (go-loop [] (when-let [t (<! ch)]
                                 (println "Chiming at" t)
                                 (swap! subsystem component/start)
                                 (future (Thread/sleep 5000)
                                         (swap! subsystem component/stop))
                                 (recur)))

                   (assoc this
                          :subsystem subsystem
                          :ch ch)))))

  (stop [this]
    (stopping this
              :on subsystem
              :action #(do
                         (async/close! ch)
                         (component/stop @subsystem)
                         (assoc this
                                :ch nil
                                :subsystem nil)))))
