(ns gametimer.util
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [cljs.core.async :as async :refer [chan put!]]
            [goog.events :as events]))

;; This is a om/root instrumentation function which properly sets the
;; displayName of components
;; TODO: I yanked this out of an older project of mine and forgot why
;; I resorted to defineProperty here...
(defn set-display-name [f cursor m]
  (let [d (or (:descriptor m) om/pure-descriptor)
        name (str/replace (.-name f) #"\$" ".")]
    (.defineProperty js/Object d "displayName"
                     #js {"configurable" true
                          "enumerable" true
                          "get" #(-> name)})
    (om/build* f cursor (assoc m :descriptor d))))

(defn by-id
  "Gets an element by Id. A wrapper document.getElementById"
  [id]
  (. js/document (getElementById id)))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))
