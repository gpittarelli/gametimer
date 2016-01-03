(ns gametimer.pages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [gametimer.history :refer [navigate! back!]]
            [taoensso.sente :as sente :refer [cb-success?]]))

(defonce groups-chan (chan))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/api/groups/channel-socket"
                                  {:type :auto
                                   :wrap-recv-evs? false})]
  (go-loop []
    (let [{:keys [id ?data]} (<! ch-recv)]
      (case id
        :chsk/state
        (om/update! data :state ?data)

        :scrim.chat/message
        (om/transact! data :log #(conj % ?data))

        :scrim.chat/players
        (om/transact! data :players #(conj % ?data))

        (println "Unhandled message type" id ?data)))
    (recur))

  (go-loop [i 0]
    (<! (async/timeout 5000))
    (send-fn [:scrim.chat/message {:message (str "hi " i)}])
    (recur (inc i))))

(defn- page-template
  [{:keys [title back?] :or {title "" back? true}}
   & contents]
  (dom/div
    (dom/header
      (when back?
        (dom/button {:on-click back! :class "back"} "Back"))
      (dom/h1 title))
    contents))

(defcomponent start-page [data owner]
  (render [_]
    (page-template
     {:title "Game Timer" :back? false}

     (dom/button
      {:on-click #(navigate! "group/create")}
      "New Group")
     (dom/button
      {:on-click #(navigate! "group/join")}
      "Join Group"))))

(defcomponent group-create [data owner]
  (render [_]
    (page-template
     {:title "Group Create"}

     (dom/label {:for "name"} "Your Name")
     (dom/input {:name "name" :type "text"})
     (dom/br)
     (dom/label {:for "turn-seconds"} "Seconds per turn")
     (dom/input {:name "turn-seconds" :type "number"})
     (dom/br)
     (dom/input {:name "submit" :type "submit"}))))

(defcomponent group-join [data owner]
  (render [_]
    (page-template
     {:title "Group Join"}

     (dom/label {:for "name"} "Your Name")
     (dom/input {:name "name" :type "text"})
     (dom/br)
     (dom/label {:for "group"} "Group ID")
     (dom/input {:name "group" :type "number"})
     (dom/br)
     (dom/input {:name "submit" :type "submit"}))))

(defcomponent group [data owner]
  (render [_]
    (page-template {:title "Group"})))
