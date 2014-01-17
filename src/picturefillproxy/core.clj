(ns picturefillproxy.core
  (:gen-class)
  (:require [picturefillproxy.resize :as resize])
  (:use org.httpkit.server
        [ring.middleware.file :only [wrap-file]]
        [clojure.tools.cli :refer [parse-opts]]))

(defn send404
  [channel]
  (send! channel {:status 404
                  :body "not found"}))


(defn async-handler [path ring-request]
  (with-channel ring-request channel
    (if (.endsWith (:uri ring-request) ".jpg")
      (let [result (resize/get-stream path (:uri ring-request))]
        (if (nil? result)
          (send404 channel)
          (send! channel {:status 200
                          :headers {"Content-Type" "image/jpg"}
                          :body result})))
      (send404 channel))))
  
(defn start-server 
  [port path]
  (let [handler (partial async-handler path)]
    (println "server started")
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
    (start-server (:port options)  (:path options))))


