(ns notify.spec.domain-model
  (:require [clojure.spec.alpha :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Categories                          ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def category? #{:sports :finance :movies})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Channels                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def channel? #{:sms :email :push-notification})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Messages                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def :message/content string?)

(s/valid? inst? (s/inst-in #inst "1970" #inst "9999"))

(s/def :message/published-date inst?)

(s/def :message/category category?)

(s/def :published/message (s/keys :req [:message/content
                                        :message/published-date
                                        :message/category]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Users                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(s/def :user/id int?)

(s/def :user/name string?)

(s/def :user/email string?)

(s/def :user/subscribed (s/coll-of :message/category))

(s/def :user/channels (s/coll-of :notification/channel))

(s/def :user/user (s/keys :req [:user/id
                                :user/name
                                :user/email
                                :user/subscribed
                                :user/channels]
                          :opt [:user/phone-number]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Notifications                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def :notification/channel channel?)

(s/def :user/email string?)

(s/def :sent/notification (s/keys :req [:notification/channel
                                        :user/id
                                        :user/name
                                        :user/email
                                        :message/slug
                                        :message/content
                                        :message/category
                                        :message/published-date]
                                  :opt [:user/phone-number]))

(defn validate-message [message]
  true)