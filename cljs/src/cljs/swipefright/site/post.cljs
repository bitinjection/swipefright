(ns swipefright.site.post
  (:require [reagent.session :as session]
            [clojure.string :as s]
            [swipefright.site.landing :as landing]))

(defn format-post []
  (let [post-info (session/get :post)]
    [:div.container.text-center
     [:div.post-title
      [:h3 (:title post-info)]
      [:h6 (:caption post-info)]]
     [:div.post
      [:img 
       {:src (str
               "/images/posts/"
               (-> post-info
                   :images
                   first
                   :image))
        :class (:class post-info)
        :on-load (fn [e]
                   (let [loading-image? (not (s/includes? (-> e .-target .-src) "loading.gif"))]
                     (if loading-image?
                       (session/swap! assoc-in [:post :class] "post-image"))
                     (session/swap! assoc :jumbotron :post)))}]]]))
