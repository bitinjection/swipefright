(ns swipefright.site.post
  (:require [reagent.session :as session]
            [reagent.core :as r]
            [clojure.string :as s]
            [swipefright.site.landing :as landing]))


(defn format-post [random-post]
  (let [posts (r/atom  0)
        back-arrow-class (r/atom "faded-arrow")]
    (fn [random-post]
      (let [post-info (session/get :post)]
        [:div
         [:div.d-flex.justify-content-center
          [:div.post-title
           [:h3 (:title post-info)]
           [:h6 (:caption post-info)]]]
         [:div.d-flex.flex-row.justify-content-center.post
          [:div.d-flex {:style {:position "relative" :min-width "325px" :width "25%"}}
           [:div#random-left-pane {:on-click #(do (if (> @posts 0) (do (swap! posts dec) (js/window.history.back))) js/window.history.back) :style {:position "absolute" :top "0px" :left "0px" :height "100%" :width "50%"}}]
           [:div#random-left-arrow.d-flex.align-items-center.p-2 {:class @back-arrow-class } [:h2 "<"]]
           [:div.p-2 
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
                           (session/swap! assoc :page :post)))}]]
           [:div#random-right-pane {:on-click #(do (swap! posts inc) (random-post)) :style {:position "absolute" :top "0px" :left "50%" :height "100%" :width "50%"}}]
           [:div#random-right-arrow.d-flex.align-items-center.p-2.faded-arrow [:h2 ">"]]]
          ]]))))
