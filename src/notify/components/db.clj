(ns notify.components.db
  (:require [com.stuartsierra.component :as component]
            [clj-postgresql.core :as pg]
            [clojure.java.jdbc :as jdbc]))

(def pool-atom (atom {}))

(defn get-pg-db
  [{:keys [host port dbname user password] :as db-conn-config}]
  (if-let [existing-db (:pool @pool-atom)]
    existing-db
    (let [pool (pg/pool :host host
                        :port port
                        :user user
                        :dbname dbname
                        :password password )]
      (swap! pool-atom assoc :pool pool)
      pool)))

(defrecord Database []
  component/Lifecycle
  (start [this]
    (let [db-conn-config (get-in this [:config :config :db-conn-config])
          conn           (get-pg-db db-conn-config)]
      (assoc this :db-conn conn)))
  (stop [this]
    (when-let [conn (:db-conn this)]
      (.close conn))
    (assoc this :db-conn nil)))

(defn new-database []
  (->Database))

(comment
  (def conn (pg/pool {:dbtype    "postgresql"
                      :classname "org.postgresql.Driver"
                      :dbname    "notify"
                      :host      "localhost"
                      :port      5432
                      :user      "postgres"
                      :password  "postgres"}))

  (jdbc/execute! conn ["INSERT INTO public.log (id) VALUES(0);"])
  (jdbc/query conn ["SELECT * FROM log"])
  )