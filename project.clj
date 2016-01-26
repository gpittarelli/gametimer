(defproject gametimer "0.1.0-SNAPSHOT"
  :description "Game turn timer web app"
  :url "http://gjp.cc/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/mit-license.php"
            :distribution :repo}

  :source-paths ["src/clj"]

  :test-paths ["test/clj"]

  :clean-targets
  ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.189" :scope "provided"]
                 [org.clojure/core.async "0.2.374"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [slester/ring-browser-caching "0.1.1"]
                 [bk/ring-gzip "0.1.1"]
                 [compojure "1.4.0"]
                 [enlive "1.1.6"]
                 [org.omcljs/om "1.0.0-alpha28"]
                 [environ "1.0.1"]
                 [prismatic/om-tools "0.3.11"]
                 [ring-transit "0.1.4"]
                 [http-kit "2.1.18"]
                 [com.taoensso/sente "1.7.0"]
                 [cljs-http "0.1.39"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-environ "1.0.1"]
            [lein-less "1.7.3"]]

  :min-lein-version "2.5.0"

  :uberjar-name "gametimer.jar"

  :cljsbuild
  {:builds {:app {:source-paths ["src/cljs"]
                  :compiler {:output-to     "resources/public/js/app.js"
                             :output-dir    "resources/public/js/out"
                             :source-map    "resources/public/js/out.js.map"
                             :optimizations :none
                             :pretty-print  true}}}}


  :less {:source-paths ["src/less"]
         :target-path "resources/public/css"}

  :figwheel
  {:css-dirs ["resources/public/css"]
   :builds
   [{:id "dev"
     :source-paths ["src/cljs" "env/dev/cljs"]
     :compiler {:output-to            "resources/public/js/app.js"
                :output-dir           "resources/public/js/out"
                :source-map           true
                :optimizations        :none
                :source-map-timestamp true}}]}

  :profiles
  {:dev {:source-paths ["env/dev/clj"]
         :test-paths ["test/clj"]

         :dependencies [[figwheel "0.5.0-2"]
                        [clojurescript-build "0.1.9"]
                        [figwheel-sidecar "0.5.0-2"]
                        [com.cemerick/piggieback "0.2.1"]
                         ^:replace [org.clojure/tools.nrepl "0.2.12"]]

         :repl-options {:init-ns gametimer.server
                        :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

         :plugins [[lein-figwheel "0.5.0-2"]]

         :figwheel
         {:http-server-root "public"
          :server-port 3449
          :css-dirs ["resources/public/css"]
          :ring-handler gametimer.server/http-handler}

         :env {:is-dev true
               :browser-caching {"text/javascript" 0
                                 "text/html" 0}}

         :cljsbuild
         {:test-commands
          { "test" ["phantomjs"
                    "env/test/js/unit-test.js"
                    "env/test/unit-test.html"] }
          :builds
          {:app {:source-paths ["env/dev/cljs"]}
           :test {:source-paths ["src/cljs" "test/cljs"]
                  :compiler {:output-to     "resources/public/js/app_test.js"
                             :output-dir    "resources/public/js/test"
                             :source-map    "resources/public/js/test.js.map"
                             :optimizations :whitespace
                             :pretty-print  false}
                  :notify-command
                  ["phantomjs" "bin/speclj" "resources/public/js/app_test.js"]
                  }}}}

   :uberjar {:source-paths ["env/prod/clj"]
             :hooks [leiningen.cljsbuild leiningen.less]
             :env {:production true
                   :browser-caching {"text/javascript" 604800
                                     "text/html" 0}}
             :omit-source true
             :aot :all
             :main gametimer.server
             :cljsbuild {:builds {:app
                                  {:source-paths ["env/prod/cljs"]
                                   :compiler
                                   {:optimizations :advanced
                                    :pretty-print false}}}}}})
