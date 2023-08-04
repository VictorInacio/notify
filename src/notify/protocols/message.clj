(ns notify.protocols.message
  (:require [notify.spec.domain-model :as spec]
            [notify.components.users :as users]
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

(defn send-message
  [db-conn message]
  (let [subscribed-users (filterv (fn [user]
                                    (contains? (set (user :user/subscribed)) (message :category))) users/users)
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
