(ns gametimer.util)

(defn ignore-trailing-slash
  "Modifies the request uri before calling the handler.
  Removes a single trailing slash from the end of the uri if present.

  Useful for handling optional trailing slashes until Compojure's
  route matching syntax supports regex. Adapted from
  http://stackoverflow.com/questions/8380468/compojure-regex-for-matching-a-trailing-slash"
  [handler]
  (fn [{uri :uri :as request}]
    (handler
     (assoc request :uri (cond (= "/" uri) uri
                               (.endsWith uri "/") (apply str (butlast uri))
                               :else uri)))))
