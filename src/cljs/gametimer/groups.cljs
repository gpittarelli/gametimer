(ns gametimer.groups
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [taoensso.sente :as sente :refer [cb-success?]]
            [cljs-http.client :as http]))

(defn create [time]
  (http/post "/api/groups"
             {:transit-params {:turn-time time}}))
