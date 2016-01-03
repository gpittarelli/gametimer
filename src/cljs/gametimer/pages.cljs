(ns gametimer.pages
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [gametimer.history :refer [navigate! back!]]))

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
