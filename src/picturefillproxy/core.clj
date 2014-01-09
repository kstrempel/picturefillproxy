(ns picturefillproxy.core
  (:gen-class)
  (:require [picturefillproxy.resize :as resize])
  (:use org.httpkit.server
        [ring.middleware.file :only [wrap-file]]
        [compojure.route :only [files not-found ]]
        [compojure.handler :only [site]]
        [compojure.core :only [defroutes routes GET POST DELETE ANY context]]))

(defn async-handler [path ring-request]
  (with-channel ring-request channel
    (if (.endsWith (:uri ring-request) ".jpg")
      (send! channel {:status 200
                      :headers {"Content-Type" "image/jpg"}
                      :body (resize/get-stream path (:uri ring-request))}))))   

(defn start-server 
  [port path]
  (let [handler (partial async-handler path)]
    (prn "web server running")
    (run-server (wrap-file handler path)
                {:port port})))

(defn -main
  [& args]
  (start-server 8080 "/Users/kai/Projects/picturefillproxy/example"))


