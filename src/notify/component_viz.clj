(ns notify.component-viz
  (:require [com.walmartlabs.system-viz :refer [visualize-system]]
            [com.stuartsierra.component :as component]
            [notify.core :as core]))


(visualize-system (core/new-sys :dev))
