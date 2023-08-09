(ns notify.core
  (:require [clojure.data.json :as json]
            [com.stuartsierra.component :as component]
            [notify.components.users-repository :as users]
            [notify.components.config :as config]
            [notify.components.db :as db]
            [notify.components.server :as server])
  (:gen-class))

(defn new-sys [profile]
  (println "Starting system with profile: " profile)
  (component/system-map
    :config (config/new-config)
    :users (users/new-users)
    :db-conn (component/using (db/new-database) [:config])
    :web-server (component/using (server/new-server) [:db-conn :users])))

(defonce sys (atom nil))

(defn -main [& args]
  (reset! sys (component/start (new-sys :dev))))

(comment
  (-main)

  (component/stop @sys)

  (->> (deref sys)
       (into {})
       ;keys
       :web-server
       :web-server
       deref
       )

  (require '[io.pedestal.test :as test-http]
           '[io.pedestal.http :as http]
           '[clojure.data.json :as json])

  (defn test-post [server verb url body]
    (test-http/response-for (:io.pedestal.http/service-fn @server) verb url :body body))

  (defn test-request [server verb url]
    (test-http/response-for (:io.pedestal.http/service-fn @server) verb url))

  (def web-server (-> sys
                      deref
                      :web-server
                      :web-server))

  (def json-payload (json/write-str {"category" "movies"
                                     "content"  "abcdef!!"}))

  (test-request web-server :get "/")
  (test-post web-server :post "/notify" json-payload)

  )