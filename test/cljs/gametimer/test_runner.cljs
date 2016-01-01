(ns gametimer.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [gametimer.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful? (run-tests 'gametimer.core-test))
    0
    1))
