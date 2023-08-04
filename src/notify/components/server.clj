(ns notify.components.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.interceptor :as i]
            [io.pedestal.http.route :as route]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [notify.protocols.message :as msg]))

(def routes
  (route/expand-routes
    #{["/history" :get (i/interceptor
                         {:name  :history
                          :enter (fn [context]
                                   (let [db-conn (:db-conn context)]
                                     (assoc context :response {:status 200
                                                               :body   (msg/get-history db-conn)})))}) :route-name :history]
      ["/notify" :post (i/interceptor
                         {:name  :notify
                          :enter (fn [context]
                                   (let [db-conn (:db-conn context)
                                         message (-> (get-in context [:request :body])
                                                     slurp
                                                     (json/read-str :key-fn keyword))]
                                     (assoc context :response {:status 200
                                                               :body   (msg/send-message db-conn message)})))}) :route-name :notify]}))

(defonce server (atom nil))

(defn start-server [service-map]
  (reset! server (http/start (http/create-server service-map))))

(defn stop-server []
  (http/stop @server))

(defn restart-server [service-map]
  (stop-server)
  (start-server service-map))

(defrecord WebServer []
  component/Lifecycle

  (start [this]
    (println "Starting server")
    (let [db-conn          (get-in this [:db-conn :db-conn])
          assoc-store      (fn [context]
                             (assoc context :db-conn db-conn))
          db-interceptor   {:name  :db-interceptor
                            :enter assoc-store}
          service-map-base {::http/routes routes
                            ::http/port   9999
                            ::http/type   :jetty
                            ::http/join?  false}
          service-map      (-> service-map-base
                               (http/default-interceptors)
                               (update ::http/interceptors conj (i/interceptor db-interceptor)))
          _                (start-server service-map)]
      (try
        (assoc this :web-server server)
        (catch Exception e
          (println "Error executing server start: " (.getMessage e))
          (println "Trying server restart..." (.getMessage e))
          (try
            (restart-server service-map)
            (println "Server Restarted successfully!")
            (catch Exception e (println "Error executing server restart: " (.getMessage e)))))
        (finally
          (println "Server Started successfully!")))))

  (stop [this]
    (stop-server)))

(defn new-server []
  (->WebServer))
