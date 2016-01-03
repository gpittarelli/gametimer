(ns gametimer.groups
  (:require [gametimer.dev :refer [is-dev?]]
            [compojure.core :refer [POST GET defroutes context]]
            [clojure.core.async :refer [<! go-loop]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit
             :refer [sente-web-server-adapter]]))

(def ^:private groups (atom {}))

(defroutes api-routes
  (GET "/group" req
    {:a 1 :b 2 :req req})
  (POST "/group" req
    {:a 1 :b 2 :req req}))

(let [{:keys [ch-recv
              send-fn
              ajax-post-fn
              ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter)]

  (defroutes socket-routes
    (GET  "/channel-socket" req (ajax-get-or-ws-handshake-fn req))
    (POST "/channel-socket" req (ajax-post-fn req)))

  (go-loop []
    (let [{:keys [event id ?data client-id ring-req]} (<! ch-recv)]
      (case id
        (println "Unhandled message type" id ?data)))
    (recur)))

(def routes (compojure.core/routes api-routes socket-routes))
