(ns finance.handler-test
  (:require [midje.sweet :refer :all]
            [cheshire.core	:as	json]
            [ring.mock.request :as mock]
            [finance.handler :refer :all]
            [finance.db :as db]))

(facts "Dá um 'Hello World' na rota raiz"
  (let [response (app (mock/request :get "/"))]
    (fact "o status da reposta é 200"
      (:status response) => 200)
    (fact "o texto do corpo é 'Hello World'"
      (:body response) => "Hello World")))


(facts "Rota inválida não existe"
  (let [response (app (mock/request :get "/invalid"))]
    (fact "o código de erro é 404"
      (:status response) => 404)
    (fact "o texto do corpo é 'Not Found'"
      (:body response) => "Not Found")))

(facts "Initial balance is 0"
  (against-background [(json/generate-string {:balance 0}) => "{\"balance\":0}"
                       (db/balance) => 0]
    (let [response (app (mock/request :get "/balance"))]
      (fact "The format is 'application/json'"
        (get-in response [:headers "Content-Type"])
            => "application/json; charset=utf-8")
      (fact "Response status is 200"
        (:status response) => 200)
      ; (fact "The text of the body is, as JSON, with balance as key and 0 as value"
      ;   (:body response) => "{\"balance\":0}")
        )
        )
        )

(facts "Register an income with value '10' "
  (against-background (db/register {:value 10 :type "income"}) => {:id 1 :value 10 :type "income"})
  (let [response (app (-> (mock/request :post "/transactions")
                          (mock/json-body {:value 10 :type "income"})))]
    (fact "Response status is 201" (:status response) = 201)
    (fact "Body response is a JSON with id and the content" (:body response) => "{\"id\":1,\"value\":10,\"type\":\"income\"}")))