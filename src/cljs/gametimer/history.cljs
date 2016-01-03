(ns gametimer.history
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(defonce history
  (doto (History.)
    (.setEnabled true)))

(defn navigate! [token]
  (.setToken history token))

(defn get-token []
  (.getToken history))

(defn make-history-chan
  "Returns a core.async channel which will contain history tokens"
  []
  (let [history-chan (chan)]
    (events/listen history
                   EventType/NAVIGATE
                   #(put! history-chan (.-token %)))
    history-chan))
