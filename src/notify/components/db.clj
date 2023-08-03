(ns notify.components.db
  (:require [com.stuartsierra.component :as component]
            [system.components.postgres :as pg]))

(defrecord Database []
  component/Lifecycle
  (start [component]
    (let [db-conn-config (get-in component [:config :db-conn-config])
          conn           (pg/new-postgres-database db-conn-config)]
      (assoc component :connection conn)))
  (stop [component]
    (when-let [conn (:connection component)]
      (.close conn))
    (assoc component :connection nil)))

(defn new-database []
  (->Database))