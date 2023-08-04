(defproject notify "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [aero "1.1.6"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [io.pedestal/pedestal.route "0.5.10"]
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [com.stuartsierra/component "1.0.0"]
                 [org.danielsz/system "0.4.8"]
                 [walmartlabs/system-viz "0.4.0"]
                 [org.clojure/java.data "1.0.95"]
                 [com.github.seancorfield/honeysql "2.4.1045"]
                 [org.clojure/data.json "2.4.0"]
                 [migratus "1.4.9"]
                 [clj-postgresql "0.7.0" ]
                 [org.postgresql/postgresql "42.2.23"]
                 [org.clojure/java.jdbc "0.7.12"]
                 ;; FRONTEND SERVER SIDE RENDERING
                 [hiccup "1.0.5"]
                 ]
  :plugins [[migratus-lein "0.7.3"]]
  :migratus {:store         :database
             :migration-dir "migrations"
             :db            {:dbtype    "postgresql"
                             :classname "org.postgresql.Driver"
                             :dbname    "notify"
                             :host      "localhost"
                             :port      5432
                             :user      "postgres"
                             :password  "postgres"}}
  :main ^:skip-aot notify.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
