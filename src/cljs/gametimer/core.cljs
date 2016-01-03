(ns gametimer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [gametimer.history :as history :refer [make-history-chan]]
            [gametimer.pages :as pages]))

(enable-console-print!)

(defonce app-state
  (atom {:url (history/get-token)}))

(defcomponent app [data owner]
  (render [_]
    (let [page (case (:url data)
                 "group/create" pages/group-create
                 "group/join" pages/group-join
                 "group/game" pages/group
                 pages/start-page)]
      (om/build page data))))

(defn main []
  (om/root
    app
    app-state
    {:target (. js/document (getElementById "app"))})

  (let [app-cursor (om/root-cursor app-state)
        history-chan (make-history-chan)]
    (go-loop []
      (let [page-id (<! history-chan)]
        (om/update! app-cursor :url page-id))
      (recur))))
