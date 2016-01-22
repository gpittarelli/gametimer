(ns gametimer.dev
  (:require [environ.core :refer [env]]
            [net.cgrand.enlive-html :refer [set-attr prepend append html]]
            [cemerick.piggieback :as piggieback]
            [figwheel-sidecar.auto-builder :as fig-auto]
            [clojurescript-build.auto :as auto]
            [clojure.java.shell :refer [sh]]))

(def is-dev? (env :is-dev))

(def inject-devmode-html
  (comp
   (set-attr :class "is-dev")
   (prepend
    (html
     [:script {:type "text/javascript" :src "/js/out/goog/base.js"}]))
   (append
    (html
     [:script {:type "text/javascript"} "goog.require('gametimer.main')"]))))

(defn start-figwheel []
  (let [server
        (figwheel-sidecar.components.figwheel-server/start-server
         {:css-dirs ["resources/public/css"]})
        config {:builds
                [{:id "dev"
                  :source-paths ["src/cljs" "env/dev/cljs"]
                  :compiler {:output-to            "resources/public/js/app.js"
                             :output-dir           "resources/public/js/out"
                             :source-map           true
                             :optimizations        :none
                             :source-map-timestamp true}}]
                :figwheel-server server}]
    (fig-auto/autobuild* config)))

(defn start-less []
  (future
    (println "Starting less.")
    (sh "lein" "less" "auto")))
