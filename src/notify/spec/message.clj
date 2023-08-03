(ns notify.spec.message
  (:require [clojure.spec.alpha :as s]))


(s/def :publicated/message (s/keys :req [:message/slug
                                         :message/content
                                         :message/category
                                         :message/publication-date]
                                   :opt [:message/author]))

(s/def :user/entity (s/keys :req [:user/id
                                  :user/name
                                  :user/email
                                  :user/subscribed
                                  :user/channels]
                            :opt [:user/phone-number]))

(s/def :sent/notification (s/keys :req [:notification/id
                                        :notification/user
                                        :notification/channel
                                        :message/slug
                                        :message/content
                                        :message/category
                                        :message/publication-date
                                        ]
                                  :opt [:message/author]))