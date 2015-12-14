(ns user
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.streaming])
  (:require
   [clojure.data.json :as json]
   [http.async.client :as ac])
  (:import
   (twitter.callbacks.protocols AsyncStreamingCallback)))

(def my-creds (make-oauth-creds *app-consumer-key*
                                *app-consumer-secret*
                                *user-access-token*
                                *user-access-token-secret*))

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

(statuses-filter :params {:track "Borat"}
         :oauth-creds my-creds
         :callbacks *custom-streaming-callback*)
