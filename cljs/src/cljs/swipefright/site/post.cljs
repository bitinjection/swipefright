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
         [:div.d-flex.justify-content-center.mx-auto
          [:div.post-title.text-center.col-xs-10.col-lg-4.content-background.p-4
           [:h3 (:title post-info)]
           [:h6 (:caption post-info)]]]
         [:div.d-flex.flex-row.justify-content-center.post.p-2
          [:div.d-flex.post-container.col-xl-4.content-background
           [:div#random-left-pane
            {:on-click #(do (if (> @posts 0) (do (swap! posts dec) (js/window.history.back))) js/window.history.back)}]
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
           [:div#random-right-pane {:on-click #(do (swap! posts inc) (random-post))}]
           [:div#random-right-arrow.d-flex.align-items-center.p-2.faded-arrow [:h2 ">"]]]]]))))
