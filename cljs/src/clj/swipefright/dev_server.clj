(ns example.dev-server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET defroutes]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [swipefright.core :refer [loading-page]]))

(defroutes routes
  (GET "/test" [] (loading-page))
  (ANY "*" _
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (io/input-stream (io/resource "public/index.html"))}))

(def http-handler
  (-> routes
      (wrap-defaults site-defaults)))
