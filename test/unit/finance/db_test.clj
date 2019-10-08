(ns finance.db-test
    (:require   [midje.sweet    :refer  :all]
                [finance.db     :refer	:all]))

(facts "Record a transaction into an atom"
    (against-background [(before :facts (clean))]
        (fact "Transactions list starts empty"
            (count (transactions)) => 0)
        (fact "The transaction is the first registry"
            (register {:value 7 :type "income"})
                =>  {:id 1 :value 7 :type "income"}
            (count (transactions)) => 1)))

(facts "Sum up the balance by given a collection of transactions"
    (against-background [(before :facts (clean))]
        (fact "Balance is positive when there is only income"
            (register {:value 1 :type "income"})
            (register {:value 10 :type "income"})
            (register {:value 100 :type "income"})
            (register {:value 1000 :type "income"})
            (balance) => 1111)
        (fact "Balance is negative when there is only expense"
            (register {:value 2 :type "expense"})
            (register {:value 20 :type "expense"})
            (register {:value 200 :type "expense"})
            (register {:value 2000 :type "expense"})
            (balance) => -2222)
        (fact "Balance is the sum of incomes less the sum of expenses"
            (register {:value 2 :type "expense"})
            (register {:value 10 :type "income"})
            (register {:value 200 :type "expense"})
            (register {:value 1000 :type "income"})
            (balance) => 808)))