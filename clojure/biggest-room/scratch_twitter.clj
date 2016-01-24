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
   (twitter.callbacks.protocols AsyncStreamingCallback SyncSingleCallback)))


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

(defn hack-body-part [_ baos]
  (try
    (-> baos
        str
        json/read-json
        :text)
    (catch Exception exn (.getMessage exn))
    )
  )

(defn hack-body-part [thing baos]
  (try
    {:thing thing
     :data (-> baos str)}
    (catch Exception exn (.getMessage exn))
    )
  )

; retrieves the user stream, waits 1 minute and then cancels the async call
(def ^:dynamic *response* (user-stream
                           :oauth-creds my-creds
                           :callbacks (AsyncStreamingCallback. #_ (comp println #(:text %) json/read-json #(str %2))
                                                               #(swap! A conj (hack-body-part %1 %2))
                                                               (comp println response-return-everything)
                                                               exception-print)))

(def ^:dynamic *response* (statuses-filter
                           :oauth-creds my-creds
                           :params {:track "Trump"}
                           :callbacks (AsyncStreamingCallback. #_ (comp println #(:text %) json/read-json #(str %2))
                                                               #(swap! A conj (hack-body-part %1 %2))
                                                               (comp println response-return-everything)
                                                               exception-print)))

(def ^:dynamic *response* (statuses-sample
                           :oauth-creds my-creds
                           :callbacks (AsyncStreamingCallback. #_ (comp println #(:text %) json/read-json #(str %2))
                                                               #(swap! A conj (hack-body-part %1 %2))
                                                               (comp println response-return-everything)
                                                               exception-print)))

(-> (deref user/A)
    (nth 0)
    :thing
    )

(-> (deref user/A)
    (nth 5)
    :data
    )

(-> (deref user/A)
    count
)

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


;;---

(friendships-show :oauth-creds my-creds
                  :callbacks (SyncSingleCallback. response-return-body
                                                  response-throw-error
                                                  exception-rethrow)
                  :params {:target-screen-name "cassieldotcom"})

(statuses-sample :oauth-creds my-creds
                 :callbacks (SyncSingleCallback. response-return-body
                                                 response-throw-error
                                                 exception-rethrow))
