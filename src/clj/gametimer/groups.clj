(ns gametimer.groups
  (:require [gametimer.dev :refer [is-dev?]]
            [ring.util.response :refer [response status]]
            [compojure.core :refer [POST GET defroutes context]]
            [clojure.core.async :refer [<! go-loop]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit
             :refer [sente-web-server-adapter]]))

(defonce ^:private groups (atom {}))

(let [counter (atom 0)]
  (defn- gen-unique-group-id! []
    (swap! counter inc)))

(defn- required [s] s)

(defn- as-int
  "Parse a string into an integer, or nil if the string cannot be parsed."
  [s]
  (cond
    (number? s) (int s)
    (string? s) (try
                  (Long/parseLong s)
                  (catch NumberFormatException _ nil))
    :else nil))

(defroutes routes
  (compojure.core/routes
   ;; Create a new group
   (POST "/" [turn-time :<< as-int]
     (let [group-id (gen-unique-group-id!)
           new-group {:turn-time turn-time}]
       (swap! groups assoc group-id new-group)
       (response {:group-id group-id})))

   (let [{:keys [ch-recv
                 send-fn
                 ajax-post-fn
                 ajax-get-or-ws-handshake-fn
                 connected-uids]}
         (sente/make-channel-socket! sente-web-server-adapter)]

     (go-loop []
       (let [{:keys [event id ?data client-id ring-req]} (<! ch-recv)]
         (case id
           (println "Unhandled message type" id ?data)))
       (recur))

     ;; Per-group routes
     (context "/:group-id{[0-9]+}" [group-id :<< as-int]
       ;; Test if a group exists
       (GET "/" req
         (if (contains? @groups group-id)
           (ajax-get-or-ws-handshake-fn req)
           (status (response "Group does not exist") 409)))

       (POST "/" req
         (if (contains? @groups group-id)
           (ajax-post-fn req)
           (status (response "Group does not exist") 409)))))))
