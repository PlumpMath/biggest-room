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



(:entries (:config S))


(def twitter (get-in S [:config :entries :twitter]))

(def my-creds (make-oauth-creds (:app-consumer-key twitter)
                                (:app-consumer-secret twitter)
                                (:user-access-token twitter)
                                (:user-access-token-secret twitter)))

user/my-creds

(users-show :oauth-creds my-creds :params {:screen-name "cassieldotcom"})

(def A (atom nil))

; retrieves the user stream, waits 1 minute and then cancels the async call
(def ^:dynamic *response* (user-stream
                           :oauth-creds my-creds
                           :callbacks (AsyncStreamingCallback. #_ (comp println #(:text %) json/read-json #(str %2))
                                                               (fn [& x] (reset! A x))
                                                               (comp println response-return-everything)
                                                               exception-print)))

(nth @A 0)

(-> (nth @A 1)
    str
    json/read-json
    )

*response*
(meta *response*)
((:cancel (meta *response*)))

(:body (friendships-show :oauth-creds my-creds
                        :params {:target-screen-name "cassieldotcom"}))


((fn [& x] x) 1 2)


; supply a callback that only prints the text of the status
(def ^:dynamic
     *custom-streaming-callback*
     (AsyncStreamingCallback. (comp println #(:text %) json/read-json #(str %2))
                              (comp println response-return-everything)
                              exception-print))

(def X (statuses-filter :params {:track "hello"}
                        :oauth-creds my-creds
                        :callbacks *custom-streaming-callback*))

(meta X)
((:cancel (meta X)))

(def X (user-stream :oauth-creds my-creds
                    :callbacks *custom-streaming-callback*))

(:body X)

*custom-streaming-callback*

(println "A")
