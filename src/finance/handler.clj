(ns finance.handler
  (:require [compojure.core           :refer  :all] 
            [compojure.route          :as     route]
          	[cheshire.core	          :as     json]
            [ring.middleware.defaults :refer  [wrap-defaults api-defaults]]
            [ring.middleware.json	    :refer	[wrap-json-body]]
            [finance.db               :as db]))

(defn as-json [content & [status]]
  {
    :status (or status 200)
    :headers { "Content-Type" "application/json; charset=utf-8" } 
    :body (json/generate-string content)})

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/balance" [] 	(as-json {:balance (db/balance)}))
  (POST "/transaction" [] {})
  (POST "/transactions" request (-> (db/register (:body request)) (as-json)))
  (route/not-found "Not Found"))

  (def app
		(-> (wrap-defaults app-routes api-defaults)
        (wrap-json-body	{:keywords?	true	:bigdecimals?	true})))
