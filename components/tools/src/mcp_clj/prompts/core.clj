(ns mcp-clj.prompts.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn- prompts-dir
  []
  (io/file "prompts"))

(defn load-prompt
  "Loads a prompt from an EDN file in the prompts directory."
  [prompt-name]
  (let [f (io/file (prompts-dir) (str prompt-name ".edn"))]
    (when (.exists f)
      (edn/read-string (slurp f)))))

(defn save-prompt
  "Saves a prompt to an EDN file in the prompts directory."
  [prompt-name content]
  (let [f (io/file (prompts-dir) (str prompt-name ".edn"))]
    (spit f (pr-str content))))
