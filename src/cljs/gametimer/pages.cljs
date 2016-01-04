(ns gametimer.pages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! chan put! timeout]]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [taoensso.sente :as sente :refer [cb-success?]]
            [gametimer.history :refer [navigate! back!]]
            [gametimer.util :refer [listen]]
            [gametimer.groups :as groups]))

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
  (init-state [_]
    {:submit-disabled? false})

  (render-state [_ {:keys [submit-disabled?]}]
    (page-template
     {:title "Group Create"}

     (dom/label {:for "name"} "Your Name")
     (dom/input {:ref "name" :name "name" :type "text"})
     (dom/br)
     (dom/label {:for "turn-seconds"} "Seconds per turn")
     (dom/input {:ref "turn-seconds" :name "turn-seconds" :type "number"})
     (dom/br)
     (dom/input
      {:ref "submit" :name "submit" :type "submit"
       :disabled submit-disabled?})))

  (did-mount [_]
    (let [name-input (om/get-node owner "name")
          turn-seconds-input (om/get-node owner "turn-seconds")
          submit-button (om/get-node owner "submit")
          clicks (listen submit-button "click")]
      (go-loop []
        (<! clicks)
        (om/update! data :username (.-value name-input))
        (om/set-state! owner :submit-disabled? true)
        (let [res (<! (groups/create (.-value turn-seconds-input)))]
          (when (:success res)
            (om/update! data :group-id (get-in res [:body :group-id]))
            (navigate! "group")))
        (om/set-state! owner :submit-disabled? false)
        (recur)))))

(defcomponent group-join [data owner]
  (init-state [_]
    {:submit-disabled? false})

  (render [_]
    (page-template
     {:title "Group Join"}

     (dom/label {:for "name"} "Your Name")
     (dom/input {:ref "name" :name "name" :type "text"})
     (dom/br)
     (dom/label {:for "group"} "Group ID")
     (dom/input {:ref "group-id" :name "group" :type "number"})
     (dom/br)
     (dom/input
      {:ref "submit" :name "submit" :type "submit"
       :disabled submit-disabled?})))

  (did-mount [_]
    (let [name-input (om/get-node owner "name")
          group-id-input (om/get-node owner "group-id")
          submit-button (om/get-node owner "submit")
          clicks (listen submit-button "click")]
      (go-loop []
        (<! clicks)
        (om/update! data :username (.-value name-input))
        (om/update! data :group-id (.-value group-id-input))
        (navigate! "group")
        (recur)))))

(defcomponent group [data owner]
  (init-state [_]
    (let [{:keys [chsk ch-recv send-fn state]}
          (sente/make-channel-socket!
           (str "/api/groups/" (:group-id data) "/")
           {:type :auto :wrap-recv-evs? false})]
      (go-loop []
        (let [{:keys [id ?data]} (<! ch-recv)]
          (case id
            (println "Unhandled message type" id ?data)))
        (recur)))
    {:submit-disabled? false})

  (render [_]
    (page-template
     {:title (str "Group " (:group-id data))}
     (dom/h2 (str "I am " (:username data))))))
