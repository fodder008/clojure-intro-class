(ns errors.core_test
   (:use [clojure.test]
         [errors.core]
         [errors.dictionaries]
         [errors.messageobj]))

(def simple-non-match-exception (java.lang.Exception. "Test Message"))
(def get-pretty-message (ns-resolve 'errors.core 'get-pretty-message))
(def class-cast-exception (java.lang.ClassCastException. "oneType cannot be cast to anotherType"))
(def bigint-illegal-arg-ex (java.lang.IllegalArgumentException. "contains? not supported on type: clojure.lang.BigInt"))
(def best-approximation (ns-resolve 'errors.dictionaries 'best-approximation))
(def incorrect-let-exception (try (eval (read-string "(let [l 1 s] (println l))")) (catch Exception e e)))

(defn get-pretty-message-string [e]
   (-> e get-pretty-message get-all-text)
   )

(deftest test-best-approximation
   (is (= "unrecognized type oneType" (best-approximation "oneType")))
   (is (= "a number" (best-approximation "clojure.lang.BigInt")))
   ;(is (= "unrecognized type clojure.lang.LittleInt" (best-approximation "clojure.lang.LittleInt"))) This triggers a classNotFoundException since there are periods in the type name 
   )

(deftest test-get-pretty-message
   (is (= "Test Message" (get-pretty-message-string simple-non-match-exception)))
   (is (= "Attempted to use unrecognized type oneType, but unrecognized type anotherType was expected." (get-pretty-message-string class-cast-exception)))
   (is (= "Function contains? does not allow a number as an argument" (get-pretty-message-string bigint-illegal-arg-ex)))
   (is (= "A parameter for a let is missing a binding on line  in the file errors.core_test" (get-pretty-message-string incorrect-let-exception)))
   )