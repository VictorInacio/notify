(ns notify.components.config
  (:require [aero.core :as aero]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]))

(defrecord Config []
  component/Lifecycle

  (start [this]
    (let [config (aero/read-config (io/resource "config.edn"))]
      (assoc this :config config)))

  (stop [this]
    (dissoc this :config)))

(defn new-config []
  (->Config))