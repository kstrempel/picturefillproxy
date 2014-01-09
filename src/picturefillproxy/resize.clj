(ns picturefillproxy.resize
  (:use [clojure.java.io])
  (:require [mikera.image.core :as img])
  (:import [java.awt.image BufferedImage]
           [java.io FileInputStream]))
    
(defn- load-image
  [filename]
  (javax.imageio.ImageIO/read (file filename)))

(defn- write-image
  [image format filename]
  (javax.imageio.ImageIO/write image format (file filename)))

(defn resize 
  [path imagename]
  (let [[_ name percent_str extension] (re-matches #"(.*)_([0-9]+)\.(.*)" imagename)
        filename (str name "." extension)
        percent  (if (nil? percent_str) 
                   100
                   (read-string percent_str))
        image    (load-image (str path filename))
        width    (.getWidth image)
        height   (.getHeight image)
        factor   (/ percent width)
        new-width  (int (* width factor))
        new-height (int (* height factor))
        scaled     (img/scale-image image new-width new-height)]
    (write-image scaled extension (str path imagename))))

(defn get-stream 
  [path filename]
  (let [filepath (str path filename)]
    (when (not (.exists (file filename)))
      (resize path filename))
    (let [filesize (.length (file filepath))]
      (with-open [in (input-stream filepath)]
        (let [buf (byte-array filesize)
              n (.read in buf)]
          (org.httpkit.BytesInputStream. buf filesize))))))


        

