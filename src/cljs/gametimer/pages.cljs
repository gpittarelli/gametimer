(ns gametimer.pages
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [gametimer.history :refer [navigate!]]))

(defn- page-template [title & contents]
  (dom/div
    (dom/h1 "Game Timer")
    contents
    ))

(defcomponent start-page [data owner]
  (render [_]
    (page-template
     "Game Timer"

     (dom/button
      {:on-click #(navigate! "group/create")}
      "New Group")
     (dom/button
      {:on-click #(navigate! "group/join")}
      "Join Group"))))

(defcomponent group-create [data owner]
  (render [_]
    (page-template
     "Group Create"

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
     "Group Join"

     (dom/label {:for "name"} "Your Name")
     (dom/input {:name "name" :type "text"})
     (dom/br)
     (dom/label {:for "group"} "Group ID")
     (dom/input {:name "group" :type "number"})
     (dom/br)
     (dom/input {:name "submit" :type "submit"}))))

(defcomponent group [data owner]
  (render [_]
    (page-template "Group")))
