(ns picturefillproxy.core
  (:gen-class)
  (:require [picturefillproxy.resize :as resize])
  (:use org.httpkit.server
        [ring.middleware.file :only [wrap-file]]
        [clojure.tools.cli :refer [parse-opts]]))

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

(def cli-options
  ;; An option with a required argument
  [["-p" "--port PORT" "Port number"
    :default 8080
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ;; A non-idempotent option
   ["-P" "--path PATH" "Web path"
    :default "."]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])


(defn -main
  [& args]
  (let [parse-result (parse-opts args cli-options)
        options (:options parse-result)]
    (prn options)
    (start-server (:port options)  (:path options))))


