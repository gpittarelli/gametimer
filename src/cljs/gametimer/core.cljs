(ns gametimer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [gametimer.history
             :as history
             :refer [make-history-chan navigate!]]))

(enable-console-print!)

(defonce app-state
  (atom {:url (history/get-token)
         :text "Hello Chestnut!"}))

(defcomponent app [data owner]
  (render [_]
    (dom/div
      (dom/h1 (:text data) (:url data))
      (dom/a {:on-click #(navigate! "abc")} "abc")
      (dom/br)
      (dom/a {:on-click #(navigate! "def")} "def")
      (dom/br)
      (dom/a {:on-click #(navigate! "ghi")} "ghi"))))

(defn main []
  (om/root
    app
    app-state
    {:target (. js/document (getElementById "app"))})

  (let [app-cursor (om/root-cursor app-state)
        history-chan (make-history-chan)]
    (go-loop []
      (let [history-event (<! history-chan)]
        (om/update! app-cursor :url %))
      (recur))))
