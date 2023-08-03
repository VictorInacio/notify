(ns notify.core
  (:require [com.stuartsierra.component :as component]
            [system.components.postgres :as pg]
            [notify.components.server :as server])
  (:import (org.testcontainers.containers PostgreSQLContainer)
           (org.testcontainers.utility DockerImageName))
  (:gen-class))

(def config {:port 4567
             :pg-config (assoc pg/DEFAULT-DB-SPEC
                          :subname "component_example"
                          :user "component_example"
                          :password "component_example")})

(defn new-sys [db-config]
  (component/system-map
    :db-conn (pg/new-postgres-database db-config)
    :web-server (component/using
                  (server/new-server)
                  [:db-conn])))

(def sys (new-sys config))

(defn -main [& args]
  (reset! sys (component/start new-sys)))

(comment
  (-main)

  )