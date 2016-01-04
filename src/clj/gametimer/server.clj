(ns gametimer.server
  (:require [clojure.java.io :as io]
            [gametimer.dev :refer [is-dev? inject-devmode-html
                                   start-figwheel start-less]]
            [compojure.core :refer [GET defroutes context]]
            [compojure.route :refer [resources not-found]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.browser-caching :refer [wrap-browser-caching]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.transit :refer [wrap-transit-response
                                             wrap-transit-params]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [gametimer.util :refer [wrap-logging ignore-trailing-slash]]
            [gametimer.groups :as groups])
  (:gen-class))

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (context "/api" []
    (context "/groups" [] groups/routes))
  (GET "/" [] (page))
  (not-found "Unknown URI"))

(defn- wrap-browser-caching-opts [handler]
  (wrap-browser-caching handler (or (env :browser-caching) {})))

(def http-handler
  (cond-> routes
    is-dev? wrap-logging
    true (wrap-transit-params {:opts {}})
    true (wrap-transit-response {:encoding :json, :opts {}})
    true (wrap-defaults api-defaults)
    true ignore-trailing-slash
    is-dev? reload/wrap-reload
    true wrap-browser-caching-opts
    true wrap-gzip))

(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (println (format "Starting web server on port %d." port))
    (run-server #'http-handler {:port port :join? false})))

(defn run-auto-reload [& [port]]
  (auto-reload *ns*)
  (start-figwheel)
  (start-less))

(defn run [& [port]]
  (when is-dev?
    (run-auto-reload))
  (run-web-server port))

(defn -main [& [port]]
  (run port))
