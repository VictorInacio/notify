(ns notify.components.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.interceptor :as i]
            [io.pedestal.http.route :as route]
            [com.stuartsierra.component :as component]))

(defonce server (atom nil))

(defn start-server [service-map]
  (reset! server (http/start (http/create-server service-map))))

(defn stop-server []
  (http/stop @server))

(defn restart-server [service-map]
  (stop-server)
  (start-server service-map))

(def routes
  (route/expand-routes
    #{["/notify" :post (i/interceptor {:name  :notify
                                       :enter (fn [context]
                                                context)}) :route-name :notify]
      ["/history" :get (i/interceptor {:name  :history
                                        :enter (fn [context]
                                                 context)}) :route-name :history]}))

(defrecord WebServer []
  component/Lifecycle

  (start [component]
    (println "Start servidor")
    (let [assoc-store      (fn [context]
                             (assoc context :db-conn (:db-conn component)))
          db-interceptor   {:name  :db-interceptor
                            :enter assoc-store}
          service-map-base {::http/routes routes
                            ::http/port   9999
                            ::http/type   :jetty
                            ::http/join?  false}
          service-map      (-> service-map-base
                               (http/default-interceptors)
                               (update ::http/interceptors conj (i/interceptor db-interceptor)))]
      (try
        (start-server service-map)
        (println "Server Started successfully!")
        (catch Exception e
          (println "Error executing server start: " (.getMessage e))
          (println "Trying server restart..." (.getMessage e))
          (try
            (restart-server service-map)
            (println "Server Restarted successfully!")
            (catch Exception e (println "Error executing server restart: " (.getMessage e))))))))

  (stop [this]
    (stop-server)))

(defn new-server []
  (->WebServer))
