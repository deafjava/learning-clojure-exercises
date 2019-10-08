(ns	finance.test-utils
    (:require	[finance.handler    :refer  [app]]
                [ring.adapter.jetty :refer  [run-jetty]]
                [cheshire.core      :as     json]
                [clj-http.client    :as     http]))

(def server (atom nil))

(defn start-server [port]
    (swap! server 
        (fn [_] (run-jetty app {:port port :join? false}))))


(defn stop-server []
    (.stop @server))

(def standard-port 3001)

(defn url-to [route] (str "http://localhost:" standard-port route))

(def request-to (comp http/get url-to))

(defn content [route] (:body (request-to route)))

(defn content-as-json [transaction]
    {
        :content-type       :json
        :body               (json/generate-string transaction)
        :throw-exceptions   false
    })

(defn expense [value]
    (content-as-json {:value value :type "expense"}))

(defn income [value]
    (content-as-json {:value value :type "income"}))