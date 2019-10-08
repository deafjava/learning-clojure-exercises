(ns finance.db)

; (declare register)

(def registers
    (atom []))

(defn transactions []
    @registers)

(defn register [transaction]
    (let [updated-collection (swap! registers conj transaction)]
        (merge transaction {:id (count updated-collection)})))

(defn clean []
    (reset! registers []))

(defn- expense? [transaction]
    (= (:type transaction) "expense"))

(defn- calculate [accumulated transaction]
    (let [value (:value transaction)]
        (if (expense? transaction)
            (- accumulated value)
            (+ accumulated value))))


(defn balance []
    (reduce calculate 0 @registers))