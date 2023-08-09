(ns notify.components.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.interceptor :as i]
            [io.pedestal.http.route :as route]
            [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [notify.spec.domain-model :as spec]
            [notify.protocols.message :as msg]))

(def dbg-context (atom nil))

(comment
  (deref dbg-context)
  )
(def routes
  (route/expand-routes
    #{["/" :get (i/interceptor
                  {:name  :home-page
                   :enter (fn [context]
                            (let [db-conn (:db-conn context)]
                              (assoc context :response {:status  200
                                                        :headers {"Content-Type" "text/html; charset=utf-8"}
                                                        :body    (msg/render-page db-conn)})))}) :route-name :home-page]
      ["/notify" :post (i/interceptor
                         {:name  :notify
                          :enter (fn [context]
                                   (let [db-conn       (:db-conn context)
                                         users         (:users context)
                                         body          (slurp (get-in context [:request :body]))
                                         message       (-> body
                                                           (json/read-str :key-fn keyword))
                                         valid-message (spec/validate-message message)]
                                     (reset! dbg-context context)
                                     (if valid-message
                                       (assoc context :response {:status 200
                                                                 :body   (msg/send-message db-conn users message)})
                                       (assoc context :response {:status 404
                                                                 :body   "Invalid message"}))))}) :route-name :notify]}))

(defonce server (atom nil))

(defn start-server [service-map]
  (reset! server (http/start (http/create-server service-map))))

(defn stop-server []
  (http/stop @server))

(defn restart-server [service-map]
  (stop-server)
  (start-server service-map))


(def dbg (atom {}))

(comment
  (deref dbg))

(defrecord WebServer []
  component/Lifecycle

  (start [this]
    (println "Starting server")
    (let [db-conn           (get-in this [:db-conn :db-conn])
          assoc-store       (fn [context]
                              (assoc context :db-conn db-conn))
          users             (get-in this [:users])
          assoc-users       (fn [context]
                              (assoc context :users users))
          db-interceptor    {:name  :db-interceptor
                             :enter assoc-store}
          users-interceptor {:name  :users-interceptor
                             :enter assoc-users}
          service-map-base  {::http/routes         routes
                             ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
                             ::http/resource-path  "/public"
                             ::http/port           9999
                             ::http/type           :jetty
                             ::http/join?          false}
          service-map       (-> service-map-base
                                (http/default-interceptors)
                                (update ::http/interceptors conj (i/interceptor db-interceptor))
                                (update ::http/interceptors conj (i/interceptor users-interceptor)))
          _                 (start-server service-map)]
      (reset! dbg this)
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
