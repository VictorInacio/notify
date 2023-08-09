(ns notify.protocols.message
  (:require [notify.spec.domain-model :as spec]
            [com.stuartsierra.component :as component]
            [notify.components.users-repository :as users-repository]
            [clojure.data.json :as json]
            [honey.sql :as sql]
            [clojure.java.jdbc :as jdbc]
            [hiccup.core :as hiccup]))

(defn get-history [db-conn]
  (->> {:select   [:id :name :email :phone_number :channel :content :category :published_date]
        :from     [:messages]
        :order-by [[:published_date :desc]]}
       (sql/format)
       (jdbc/query db-conn)
       vec))

(defn script-view []
  [:script {:src "client.js" :defer true}])

(defn form-view []
  [:form {:id "messageForm" :onsubmit "return sendMessage(event);"}
   [:label {:for "category"} "Category:"]
   [:select {:id "category"}
    [:option {:value "sports"} "Sports"]
    [:option {:value "finance"} "Finance"]
    [:option {:value "movies"} "Movies"]]
   [:br]
   [:label {:for "message"} "Message:"]
   [:input {:type "text" :id "content" :required true}]
   [:br]
   [:button {:type "submit"} "Submit"]])

(defn render-page [db-conn]
  (let [message-history (get-history db-conn)]
    (hiccup/html
      [:html
       [:head
        [:title "Send Message"]]
       [:body
        [:h1 "Send Message"]
        (form-view)
        [:div#response]
        (script-view)
        [:table
         [:tr
          [:th "Date"]
          [:th "Message"]
          [:th "Category"]
          [:th "Channel"]
          [:th "User"]
          [:th "Email"]
          [:th "Phone"]]
         (for [message message-history]
           [:tr
            [:td (:published_date message)]
            [:td (:content message)]
            [:td (:category message)]
            [:td (:channel message)]
            [:td (:name message)]
            [:td (:email message)]
            [:td (:phone_number message)]])]]])))

(def user-keys [:user/id :user/name :user/email :user/phone-number])

(defn make-insert-query [message]
  (sql/format {:insert-into [:messages]
               :values      [message]}))

(defn persist-message [db-conn message]
  (jdbc/execute! db-conn (make-insert-query message)))

(defmulti notify :channel)

(defn send-sms [phone-number message]
  (println "Add SMS messaging service API call for:" phone-number message))

(defmethod notify "sms" [message]
  (let [phone-number (message :user/phone-number)
        message      (str (message :category) " : " (message :content))]
    (send-sms phone-number message)))

(defn send-email [email user-name message]
  (println "Add SMTP or email API call for:" email user-name message))

(defmethod notify "email" [message]
  (let [email     (message :user/email)
        user-name (message :user/name)
        message   (str (message :category) " : " (message :content))]
    (send-email email user-name message)))

(defn send-push-notification [user-id message]
  (println "Add SMTP or email API call for:" user-id message))

(defmethod notify "push-notification" [message]
  (let [user-id (message :user/id)
        message (str (message :category) " : " (message :content))]
    (send-push-notification user-id message)))

(defn send-message
  [db-conn users message]
  (let [category         (message :category)
        subscribed-users (users-repository/get-users-by-category users category)
        now              (System/currentTimeMillis)
        messages         (->> subscribed-users
                              (mapcat (fn [user]
                                        (for [channel (user :user/channels)]
                                          (merge (select-keys user user-keys)
                                                 message
                                                 {:channel        (name channel)
                                                  :published-date now})))))]
    ;; Persist message sent for history reasons
    (doseq [message messages] (persist-message db-conn message))
    ;; Main notification call to multi-method dispatching on channel
    (mapv notify messages)
    (json/write-str {:added-messages (count messages)})))

