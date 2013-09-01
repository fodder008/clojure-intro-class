(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
	      [errors.errorgui]
	      [errors.messageobj]
        [seesaw.core]))

;; Ignore stack trace entries beginning with user, clojure, or java 
(def ignore-nses #"(user|clojure|java)\..*")

;; Gets the first match out of the error dictionary 
;; based on the exception class and the message
(defn- first-match [e message]
	;(println (str (class e) " " message)) ; debugging print
	(first (filter #(and (instance? (:class %) e) (re-matches (:match %) message))
			error-dictionary)))

;; Putting together a message (perhaps should be moved to errors.dictionaries? )
(defn- get-pretty-message [e]
  (let [m (.getMessage e)
  	    message (if m m "")] ; converting an empty message from nil to ""
    (if-let [entry (first-match e message)]
  	  ((:make-preobj entry) (re-matches  (:match entry) message))
  	  (make-preobj-hashes message))))

;; Returns true if a :trace-elems element is meaningful to the student
(defn- is-meaningful-elem? [elem]
    (and 
      (:clojure elem) 
      (not (re-matches ignore-nses (:ns elem))))
  )

;; Takes in a single :trace-elems entry and produces a string for 
;; our filtered stacktrace
(defn- create-error-str [err-elem]
  (str
    "\t"
    (:ns err-elem)
    "/"
    (:fn err-elem)
    " ("
    (:file err-elem)
    " line "
    (:line err-elem)
    ")"
    )
  )

;; Creates the pre-object from a filtered stack trace
(defn- create-pre-obj [errstrs]
  (make-preobj-hashes (str "\nSequence of function calls:\n" (join "\n" errstrs)) :causes)
  )

;; Creates the full error object that is to be displayed
(defn- create-error-obj [e preobj]
    (make-obj (concat (make-preobj-hashes "ERROR: " :err) 
                      (get-pretty-message e) 
                      preobj)))

;; All together:
(defn prettify-exception [e]
  (let [preobj (->> e stacktrace/parse-exception
                      (#(filter is-meaningful-elem? (:trace-elems %)))
                      (map create-error-str)
                      (create-pre-obj))]
    (show-error (create-error-obj e preobj) e)))