(ns notify.protocols.message
  (:require [notify.spec.domain-model :as spec]
            [notify.components.users :as users]
            [clojure.data.json :as json]
            [honey.sql :as sql]
            [clojure.java.jdbc :as jdbc]))


(defn get-history [db-conn]
  (->> {:select   [:id :name :email :phone_number :channel :content :category :published_date]
        :from     [:messages]
        :order-by [[:published_date :desc]]}
       (sql/format)
       (jdbc/query db-conn)
       (json/write-str)))

(def user-keys [:user/id :user/name :user/email :user/phone-number])

(defn make-insert-query [message]
  (sql/format {:insert-into [:messages]
               :values      [message]}))

(defn send-message
  [db-conn message]
  (let [subscribed-users [(first users/users)]
        now              (System/currentTimeMillis)
        messages         (->> subscribed-users
                              (mapcat (fn [user]
                                        (for [channel (user :user/channels)]
                                          (merge (select-keys user user-keys)
                                                 message
                                                 {:channel        (name channel)
                                                  :published-date now})))))
        message-queries  (mapv make-insert-query messages)]
    (doseq [message-query message-queries]
      (jdbc/execute! db-conn message-query))
    (json/write-str {:added-messages (count message-queries)})))