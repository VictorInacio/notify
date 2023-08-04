(ns notify.components.users)


(def users [{:user/id 1
             :user/name "User1"
             :user/email "User1@mail.com"
             :user/phone-number "+11111"
             :user/subscribed ["sports" "finance" "movies"]
             :user/channels [:sms :email :push-notification]}
            {:user/id 2
             :user/name "User2"
             :user/email "User2@mail.com"
             :user/phone-number "+22222"
             :user/subscribed ["finance" "movies"]
             :user/channels [:sms :email]}
            {:user/id 3
             :user/name "User3"
             :user/email "User3@mail.com"
             :user/phone-number "+33333"
             :user/subscribed ["movies"]
             :user/channels [:sms]}])