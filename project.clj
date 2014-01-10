(defproject picturefillproxy "0.1.0-SNAPSHOT"
  :description "PictureFillProxy"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.16"]
                 [compojure "1.1.6"]
                 [ring/ring-core "1.2.1"]
                 [org.clojure/tools.cli "0.3.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/tools.cli "0.3.1"]
                 [net.mikera/imagez "0.3.1"]]
  :main picturefillproxy.core)
