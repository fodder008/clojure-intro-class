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

(deftest test-best-approximation
   (is (= "unrecognized type oneType" (best-approximation "oneType")))
   (is (= "a number" (best-approximation "clojure.lang.BigInt")))
   ;(is (= "unrecognized type clojure.lang.LittleInt" (best-approximation "clojure.lang.LittleInt"))) This triggers a classNotFoundException since there are periods in the type name 
   )

(deftest test-get-pretty-message
   (is (= "Test Message" (get-all-text (get-pretty-message simple-non-match-exception))))
   (is (= "Attempted to use unrecognized type oneType, but unrecognized type anotherType was expected." (get-all-text (get-pretty-message class-cast-exception))))
   (is (= "Function contains? does not allow a number as an argument" (get-all-text (get-pretty-message bigint-illegal-arg-ex))))
   )