(ns mcp-clj.tools.bb-test
  (:require [clojure.test :refer :all]
            [mcp-clj.tools.bb :as bb]
            #?(:bb [nrepl.core :as nrepl])))

(deftest eval-code-test
  (testing "evaluates a simple expression"
    (is (= 2 (bb/eval-code "(+ 1 1)"))))
  (testing "evaluates a more complex expression"
    (is (= 10 (bb/eval-code "(->> (range 5) (apply +))")))))

#?(:bb
   (deftest nrepl-server-test
     (testing "starts and stops the nREPL server"
       (let [port 7889
             host "localhost"]
         (future (bb/start-nrepl-server! {:port port :host host}))
         ;; Give the server a moment to start
         (Thread/sleep 1000)
         (try
           (let [client (nrepl/connect :port port :host host)]
             (is client "nREPL client should be connected")
             (let [response (first (nrepl/message client {:op "eval" :code "(+ 2 2)"}))]
               (is (= "4" (:value response)) "nREPL server should evaluate expressions")))
           (finally
             (bb/stop-nrepl-server!)))))))
