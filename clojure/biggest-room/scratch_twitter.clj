(ns user
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [twitter.api.streaming])
  (:require
   [clojure.data.json :as json]
   [http.async.client :as ac]
   [com.stuartsierra.component :as component]
   [eu.cassiel.biggest-room.main :as main])
  (:import
   (twitter.callbacks.protocols AsyncStreamingCallback)))


(reset)



(:config (:config S))


(def twitter (get-in S [:config :entries :twitter]))

(def my-creds (make-oauth-creds (:app-consumer-key twitter)
                                (:app-consumer-secret twitter)
                                (:user-access-token twitter)
                                (:user-access-token-secret twitter)))

user/my-creds

(users-show :oauth-creds my-creds :params {:screen-name "cassieldotcom"})

; retrieves the user stream, waits 1 minute and then cancels the async call
(def ^:dynamic *response* (user-stream :oauth-creds my-creds))

(meta *response*)

(Thread/sleep 60000)
((:cancel (meta *response*)))

; supply a callback that only prints the text of the status
(def ^:dynamic
     *custom-streaming-callback*
     (AsyncStreamingCallback. (comp println #(:text %) json/read-json #(str %2))
                              (comp println response-return-everything)
                              exception-print))

(statuses-filter :params {:track "Bowie"}
                 :oauth-creds my-creds
                 :callbacks *custom-streaming-callback*)

*custom-streaming-callback*
