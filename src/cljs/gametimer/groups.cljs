(ns gametimer.groups
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [put! chan alts!]]
            [taoensso.sente :as sente :refer [cb-success?]]
            [cljs-http.client :as http]))

(defn create [time]
  (http/post "/api/groups"
             {:transit-params {:turn-time time}}))

(def ^:private ctrl-chan (chan))

(defn join [group-id]
  (put! ctrl-chan {:id ::join :?data {:group-id group-id}}))

(defn- make-socket [group-id]
  (sente/make-channel-socket! (str "/api/groups/" group-id)
                              {:type :auto :wrap-recv-evs? false}))

(go-loop [socket nil]
  ;; (let [{:keys [chsk ch-recv send-fn state]} socket])

  (let [chans (remove nil? [ctrl-chan (when socket (:ch-recv socket))])
        [{:keys [id ?data]} recv-chan] (alts! chans)
        ctrl-event? (= recv-chan ctrl-chan)]
    (case id
      ::join
      (let [{:keys [group-id]} ?data]
        (println "joining group" group-id)
        (when socket (sente/chsk-destroy! (:chsk socket)))
        (let [new-socket (make-socket group-id)]
          (recur new-socket)))

      (println "Unhandled"
               (if ctrl-event? "control" "ws")
               "event:" id
               "data:" ?data)))
  (recur socket))
