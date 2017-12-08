(ns swipefright.site.post
  (:require [reagent.session :as session]
            [reagent.core :as r]
            [clojure.string :as s]
            [swipefright.site.landing :as landing]))

(defn twitter-link [title url]
  [:div 
   (let [url (-> js/window .-location .-href)
         base-link "https://twitter.com/intent/tweet?text="
         hashtag "%20%23swipefright"]
     [:a.btn.btn-primary.mt-3.mb-3 {:href (str base-link title hashtag "&url=" url) :target "_blank"}
      [:img {:src "/images/twitter.png"}]" Tweet This"])])


(defn title-text [height text]
  [:h1.d-flex.align-items-center.justify-content-center {:class (str "h-" height)} text])

(defn caption-text [height text]
  [:h4.d-flex.align-items-center.justify-content-center {:class (str "h-" height)} text])

(defn captionless-title [title]
  [title-text 100 title])

(defn full-title [title caption]
  [:div
   [title-text 50 title]
   [caption-text 50 caption]])

(defn format-post [random-post]
  (let [posts (r/atom  0)
        back-arrow-class (r/atom "faded-arrow")]
    (fn [random-post]
      (let [post-info (session/get :post)
            title (:title post-info)
            caption (:caption post-info)]
        [:div
         [:div.d-flex.justify-content-center.mx-auto
          [:div.post-title.text-center.col-xs-10.col-lg-4.content-background.p-2
           (if (or (= caption "") (= caption nil))
             [captionless-title title]
             [full-title title caption])]]
         [:div.content-background.justify-content-center.text-center.mx-auto.col-xl-4.p-0
          [:div [twitter-link title (-> js/window .-location .-href)]]
          [:div.d-flex.flex-row.justify-content-center.post
           [:div.d-flex.post-container
            [:div#random-left-pane
             {:on-click
              #(do (if (> @posts 0) (do (swap! posts dec) (js/window.history.back))) js/window.history.back)}]
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
            [:div#random-right-arrow.d-flex.align-items-center.p-2.faded-arrow [:h2 ">"]]]]
          ]]))))
