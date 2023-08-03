(ns notify.core-test
  (:require [clojure.test :as test]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test-http]
            [notify.components.server :as http-server]))

(defonce server (atom nil))


;; The fixture function to start and stop the web server
(defn start-stop-web-server-fixture [test-fn]
  (let [service-map {::http/routes http-server/routes
                     ::http/port   9999
                     ::http/type   :jetty
                     ::http/join?  false}]
    (println "Starting web server...")
    (reset! server (http-server/start-server service-map))
    (Thread/sleep 1000)                                  ; Give the server some time to start)
    (test-fn)
    (http/stop @server)))

(defn test-post [server verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))

;; Register the fixture
(test/use-fixtures :once start-stop-web-server-fixture)

;; Use the fixture with the test function
(test/deftest test-get-history
              (test/testing "Test history endpoint"
                            (test/is (= "History!" (:body (test-request server :get "/history"))))))

;; Use the fixture with the test function
(test/deftest my-web-server-test
              (test/testing "Test notify"
                            (test/is (= "Notified!" (:body (test-post server :post "/notify" ""))))))