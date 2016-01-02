(ns gametimer.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(enable-console-print!)

(defonce app-state
  (atom {:url ""
         :text "Hello Chestnut!"}))

(defonce history (History.))

(defn navigate! [fragment]
  (.setToken history fragment))

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

  (let [cursor (om/root-cursor app-state)]
    (doto history
      (events/listen EventType/NAVIGATE
                     #(do (om/update! cursor :url (.-token %))))
      (.setEnabled true))))
