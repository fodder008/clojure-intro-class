(ns errors.core
  (:require [clj-stacktrace.core :as stacktrace])
  (:use [clojure.string :only [join]]
        [errors.dictionaries]
	[errors.errorgui]
        [seesaw.core]))

;;(def ignore-nses #"(clojure|java)\..*")
(def ignore-nses #"(user|clojure|java)\..*")

(defn- first-match [e]
	(println (class e))
	(first (filter #(and (instance? (:class %) e) (re-matches (:match %) (.getMessage e)))
			error-dictionary)))

;; Putting together a message (perhaps should be moved to errors.dictionaries? )
(defn- get-pretty-message [e]
  (let [message (.getMessage e)]
  	  (if-let [entry (first-match e)]
  	  	  (clojure.string/replace message (:match entry) (:replace entry))
  	  	  message)))

;; All together:
(defn prettify-exception [e]
  (let [info (stacktrace/parse-exception e)
        cljerrs (filter #(and (:clojure %) (not (re-matches ignore-nses (:ns %))))
                        (:trace-elems info))
        errstrs (map #(str "\t" (:ns %) "/" (:fn %) " (" (:file %) " line " (:line %) ")") cljerrs)]
    (show-error (str "ERROR: " (get-pretty-message e) "\nPossible causes:\n" (join "\n" errstrs))
		e)))
