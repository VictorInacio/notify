(ns notify.core
  (:require [com.stuartsierra.component :as component]
            [notify.components.config :as config]
            [notify.components.db :as db]
            [notify.components.server :as server])
  (:gen-class))

(defn new-sys [profile]
  (println "Starting system with profile: " profile)
  (component/system-map
    :config (config/new-config)
    :db-conn (component/using (db/new-database) [:config])
    :web-server (component/using (server/new-server) [:db-conn])))

(defonce sys (atom nil))

(defn -main [& args]
  (reset! sys (component/start (new-sys :dev))))

(comment
  (-main)
  (component/stop @sys)
  )