(ns gametimer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [gametimer.history :as history :refer [make-history-chan]]
            [gametimer.pages :as pages]
            [gametimer.groups :as groups]
            [gametimer.util :refer [by-id set-display-name]]))

(enable-console-print!)

(defonce app-state
  (atom {:page pages/start-page
         :group-id nil}))

(def routes
  [{:path #"group/create" :page pages/group-create}
   {:path #"group/join" :page pages/group-join}
   {:path #"group/(\d+)" :page pages/group
    :on-navigate
    #(do
       (groups/join %)
       (om/update! (om/root-cursor app-state) :group-id %))}
   {:path #".*" :page pages/start-page}])

(defcomponent app [data owner]
  (render [_]
    (om/build (:page data) data)))

(defn main []
  (let [start-url (history/get-token)
        history-chan (make-history-chan)
        app-cursor (om/root-cursor app-state)

        on-navigate
        (fn [url]
          (let [match-route
                (fn [route]
                  (let [matches (re-matches (:path route) url)]
                    (when matches
                      (assoc route
                             :params (if (seq matches) (rest matches) [])))))
                route (some match-route routes)]
            (when (:on-navigate route)
              (apply (:on-navigate route) (:params route)))
            (om/update! app-cursor :page (:page route))))]

    (om/root app app-state
             {:target (by-id "app")
              :instrument set-display-name})

    (on-navigate start-url)
    (go-loop []
      (let [page-id (<! history-chan)]
        (on-navigate page-id))
      (recur))))
