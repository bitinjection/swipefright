(ns swipefright.controllers.index
  (:require [reagent.session :as session]
            [accountant.core :as accountant]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! >! chan put!]]
            [swipefright.validation :as validation]
            [swipefright.url :as url])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def api-url "http://swipefright.net:8880/api/")

;; Replaced functionality with nil for the time being, consider removing
(defn validate-email-input [e]
  (let [input (.-target.value e)]
    (session/swap! assoc-in [:landing :notify-email] input)
    nil))
    ;;(toggle-notify-button (validation/is-valid-email? input))))

;; Replaced functionality with nil for the time being, consider removing
(defn save-email [email]
  (go (let [response 
            (<! (http/post 
                  (str api-url "emails")
                  {:with-credentials? false
                   :json-params
                   {:email {:email (session/get-in [:landing :notify-email])}}}))]
        nil)))
        ;;(modal/modal! (site/subscribe-confirmed-modal)))))

(defn validate-email-on-enter [event]
  (let [code (.-charCode event)]
    (if (= code 13)
      (validate-email-input event))))

(def ch (chan))

(defn image-link [file]
  (let [reader ( js/FileReader.)]
    (.addEventListener 
      reader 
      "load" 
      (fn [e]
        (put! ch e)))
    (go
      (let [e (<! ch)]
        (session/swap! update-in [:uploaded-images] conj (.-target.result e)))) 
    (.readAsDataURL reader file)))

(defn upload-queued [e]
  (image-link (first e.target.files)))


(defn post-update [current json]
  (-> current
      (assoc :post
             {:title (:title json)
              :caption (:caption json)
              :images (:images json)
              :class "post-loading"})
      (assoc :jumbotron :post)))

(defn parse-post [json]
  (let [parsed (get-in json [:body :data])]
    (session/swap! post-update parsed)))

(defn fetch-post [id]
  (go 
    (let [response (<! (http/get (str api-url "posts/" (url/decode52 id))
                                   {:with-credentials? false}))
          json (js->clj response)]
      (if (:success json)
        (parse-post json))))
  nil)

(defn random-post []
  (go (let [id (get-in (<! (http/get
                                (str api-url "posts/random")
                                {:with-credentials? false}))
                          [:body :data :id])]
        
        ;; This line will trigger the relevant routing function call
        (accountant/navigate! (str "/p/" (url/encode52 id)))))
  (session/swap!  assoc
                 :post
                 {:title nil
                  :caption nil
                  :images [{ :image "loading.gif"}]
                  :class "post-loading"}))
