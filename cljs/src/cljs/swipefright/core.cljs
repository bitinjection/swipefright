(ns swipefright.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! >! chan put!]]
            [reagent-modals.modals :as modal]
            [goog.string :as gstring]
            [cljsjs.react-transition-group :as transition] 
            [swipefright.url :as url]
            [swipefright.validation :as validation]
            [swipefright.site.core :as site]
            [swipefright.site.mainpage :as main]
            [swipefright.controllers.index :as controllers]
            [swipefright.site.upload :as upload]
            ) 
  (:import
    [goog.history Html5History EventType]
    [goog Uri])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(extend-type js/FileList
  ISeqable
  (-seq [array] (array-seq array 0)))


#_(session/reset!  
  {:page :jumbotron
   :selected-image nil
   :post {:title "Loading..." :caption nil :images nil :class "post-loading"}
   :content {:body [:div "empty"]} 
   :landing {:notify-email "testerino!" :notify-button-classes "btn btn-primary disabled" } 
   :uploaded-images []
   :upload-page :upload})



(defonce page (atom #'main/home-page))

(defn current-page []
  [:div [@page]
   [modal/modal-window]])

;; -------------------------
;; Routes

(def history (Html5History.))
(.setUseFragment history false)
(.setPathPrefix history "")
(.setEnabled history true)

(defn nav! [token]
  (. history (setToken "swipefright" token)))

(secretary/defroute "/" []
  (session/swap! assoc :page :jumbotron))

(secretary/defroute "/upload" []
  (do (session/swap! assoc :page :upload)
      (session/swap! assoc :upload-page :upload)))

;; Routes like these need to be set up in on the backend
(secretary/defroute "/p/:id" [id]
  (controllers/fetch-post id))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
