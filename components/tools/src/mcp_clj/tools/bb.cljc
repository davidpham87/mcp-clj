(ns mcp-clj.tools.bb
  (:require [clojure.java.io :as io]
            [sci.core :as sci]
            #?@(:bb [[babashka.nrepl.server :as nrepl]
                     [clojure.tools.cli :as cli]])))

#?(:bb
   (do
     (defonce server (atom nil))

     (defn start-nrepl-server!
       "Starts a nREPL server given a port and a host.
   An .nrepl-port file is created in the project root.
   Blocks until the server is shut down."
       [{:keys [port host]}]
       (let [s (nrepl/start-server! {:port port
                                     :host host})]
         (reset! server s)
         (let [port-file (io/file ".nrepl-port")]
           (.deleteOnExit port-file)
           (spit port-file (:port s)))
         (println "nREPL server started on port" (:port s))
         @(promise)))

     (defn stop-nrepl-server!
       "Stops the nREPL server."
       []
       (when-let [s @server]
         (nrepl/stop-server! s)
         (reset! server nil)
         (println "nREPL server stopped")))
     (def cli-options
       [["-p" "--port PORT" "Port number"
         :default 7888
         :parse-fn #(Integer/parseInt %)]
        ["-H" "--host HOST" "Host"
         :default "localhost"]
        ["-h" "--help"]])

     (defn -main [& args]
       (let [{:keys [options summary errors]}
             (cli/parse-opts args cli-options)]
         (cond
           (:help options) (println summary)
           errors (println (clojure.string/join "\n" errors))
           :else (start-nrepl-server! options)))))
   :clj
   (do
     (defn start-nrepl-server! [_])
     (defn stop-nrepl-server! [])
     (defn -main [& _args])))

(defn eval-code
  "Evaluates a string of Clojure code."
  [code]
  (sci/eval-string code))
