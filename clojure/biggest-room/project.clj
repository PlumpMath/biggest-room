(defproject eu.cassiel/biggest-room "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-http "2.0.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[cider/cider-nrepl "0.9.1"]
            [refactor-nrepl "1.1.0"]
            [lein-ring "0.9.7"]]
  :main ^:skip-aot eu.cassiel.biggest-room
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [org.clojure/tools.nrepl "0.2.7"]]
                   :source-paths ["dev"]}
             :uberjar {:aot :all}})
