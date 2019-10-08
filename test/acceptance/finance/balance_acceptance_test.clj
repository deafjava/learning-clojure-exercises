(ns finance.balance-acceptance-test
  (:require
   [midje.sweet        :refer  :all]
   [cheshire.core      :as     json]
   [finance.test-utils :refer  :all]
   [finance.db         :as     db]
   [clj-http.client    :as     http]))

(against-background [(before :facts [(start-server standard-port)
                                     (db/clean)])
                     (after :facts (stop-server))]
                    (fact "Initial balance is '0'" :acceptance
                          (json/parse-string \t (content "/balance") true) => {:balance 0})
                    (fact "The balance is '10' after a income transaction with value of 10"
                          :acceptance
                          (http/post (url-to "/transactions")
                                     {:content-type   :json
                                      :body (json/generate-string {:value 10 :type "income"})})
                          (json/parse-string (content "/balance") true) => {:balance 10})
                    (fact "The balance is 1000 when we create two incomes of 2000 and one expense of 3000" :acceptance
                          (http/post (url-to "/transactions") (income 2000))
                          (http/post (url-to "/transactions") (income 2000))
                          (http/post (url-to "/transactions") (expense 3000))
                          (json/parse-string (content "/balance") true) => {:balance 1000})

                    (fact "Reject a transaction without a value"
                          (let [response (http/post (url-to "/transactions") (content-as-json {:type "expense"}))]
                            (:status response) => 422))

                    (fact "Reject a transaction with a negative value"
                          (let [response (http/post (url-to "/transactions") (income -100))]
                            (:status response) => 422))

                    (fact "Reject a transaction with a non-integer value"
                          (let [response (http/post (url-to "/transactions") (income "thousand"))]
                            (:status response) => 422))

                    (fact "Reject a transaction with a invalid type"
                          (let [response (http/post (url-to "/transactions") (content-as-json {:type "lottery" :value 1000000}))]
                            (:status response) => 422)))