(ns notify.components.users-repository
  (:require [com.stuartsierra.component :as component]))


(def users-mock [{:user/id           1
                  :user/name         "User1"
                  :user/email        "User1@mail.com"
                  :user/phone-number "+11111"
                  :user/subscribed   ["sports" "finance" "movies"]
                  :user/channels     [:sms :email :push-notification]}
                 {:user/id           2
                  :user/name         "User2"
                  :user/email        "User2@mail.com"
                  :user/phone-number "+22222"
                  :user/subscribed   ["finance" "movies"]
                  :user/channels     [:sms :email]}
                 {:user/id           3
                  :user/name         "User3"
                  :user/email        "User3@mail.com"
                  :user/phone-number "+33333"
                  :user/subscribed   ["movies"]
                  :user/channels     [:sms]}])


(defprotocol UserService
  (get-all-users [this])
  (get-users-by-category [this category]))


(defrecord UserRepository [users]
  component/Lifecycle
  (start [this]
    (println "Starting user repository")
    (assoc this :users users))

  (stop [this]
    (dissoc this :users))

  UserService
  (get-all-users [this]
    (:users this))

  (get-users-by-category [this category]
    (filterv (fn [user]
               (contains? (set (user :user/subscribed)) category)) (:users this))))



(defn new-users []
  (->UserRepository users-mock))


(comment

  (def usrs (->UserRepository users-mock))

  (get-all-users usrs)

  (get-users-by-category usrs "movies")
  )