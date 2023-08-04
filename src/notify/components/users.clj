(ns notify.components.users)


(def users [{:user/id 1
             :user/name "User1"
             :user/email "User1@mail.com"
             :user/phone-number "+123456789"
             :user/subscribed [:sports :finance :movies]
             :user/channels [:sms :email :push-notification]}
            {:user/id 2
             :user/name "User2"
             :user/email "User2@mail.com"
             :user/phone-number "+123456789"
             :user/subscribed [:sports :finance]
             :user/channels [:sms :email]}
            {:user/id 3
             :user/name "User3"
             :user/email "User3@mail.com"
             :user/phone-number "+123456789"
             :user/subscribed [:sports]
             :user/channels [:sms]}])