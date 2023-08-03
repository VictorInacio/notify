(ns notify.protocols.message
  (:require [notify.spec.domain-model :as spec]))


(defmulti send-message :channel)


(defmethod send-message :sms
  [message]
  )

(defmethod send-message :email
  [message]
  )

(defmethod send-message :push-notification
  [message]
  )


