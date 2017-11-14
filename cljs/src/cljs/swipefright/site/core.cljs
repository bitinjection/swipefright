(ns swipefright.site.core
  
  (:require  [reagent-modals.modals :as modal]
            [goog.string :as gstring]
            
            ) 
  )

(defn large-icon [name]
  [:div.col-12 [:i.fa.fa-4x {:class name}]])

(defn create-modal-body [icon subject body]
  [:div.modal-body.text-center
   [:div.row
   (large-icon icon)
   [:div.col-12 [:h2 subject ]]]
   [body]])

;; FIXME: Old subscribe modal that nobody used, probably remove
(defn notify-body []
  ;;(let [button-classes (get-in @app-state [:landing :notify-button-classes])]
  (let [button-classes nil]
    (fn []
      [:div
       [:div
        [:div (str "SwipeFright is still summoning"
                   "(we ain't finished making it yet)." )]]
       [:div
        [:div (str "If you'd like to be notified when it is ready,"
                   "enter your email address here." )]
        [:br]
        [:div "Follow "
         [:a
          {:href "https://twitter.com/swipefright" :target "_blank"}
          "@swipefright"]]]
       [:br]
       [:div 
        [:div.form-group 
         [:input.form-control 
          {:placeholder "email" :type "text"  
           ;;:on-key-press validate-email-on-enter 
           ;;:on-change validate-email-input
           }]]
        [:div.form-group 
         [:a 
          {;;:class 
           ;;(get-in @app-state [:landing :notify-button-classes])
           ;;:on-click save-email 
           :href "#" }
          "Notify Me" ]]]])))

(defn create-modal-header [title]
  [:div.modal-header.col-12
   [:h4.mx-auto title]
   [:button.close {:type "button" :data-dismiss "modal"}
    [:span (gstring/unescapeEntities "&times;")]]])

(defn subscribe-confirmed-modal []
  (letfn [(subscribed-body [] 
            [:div "Thanks!  We'll notify you when we're up and running!"])]
    [:div
     (create-modal-header "Thanks")
     (create-modal-body "fa-thumbs-up" "Subscribed" subscribed-body)]))

(defn subscribe-modal []
  [:div
   (create-modal-header "Summoning")
   (create-modal-body "fa-newspaper-o" "Summoning" (notify-body))])

(defn jumbotron [enter]
  [:div.container 
   [:div.jumbotron
    [:div.lead.col-12.text-center
     [:h5
      {:style  {:letter-spacing "2px" :text-transform "uppercase"}} 
      "The Swipe Rights That Haunt Your Nights" ]]
    [:br]
    [:div.row
     [:div.col-xs-12.col-lg-8.mb-5 {:style {:display "flex" :align-items "center"}}
      [:div
       [:h5
        [:ul {:style {:line-height "2.5em"}}
         [:li "Shall you drink from the cauldron of cringe?"]
         [:li "Will you be stifled by the self entitled, or hang from the pickup line twine?"]
         [:li "Do you dare dig into the darkest depths of online dating discourse?"]
         [:li "Proceed at your own peril!"]]
        ]]]
     [:div.col-md-4.text-center [:img {:src "images/logo.png"}]]
     [:div.row.col-12
      [:div.text-center.col-12
       [:hr.my-4]
       [:a.btn.btn-primary 
        {:on-click #(enter) :href "#"}
        [:i.fa.fa-superpowers {:style {:padding-right "5px"}}]
        "Enter"]]]]]])

(defn image-thumbnails [images]
  (fn []
    ;;(let [images (get-in @app-state [:uploaded-images])]
    (let [images nil]
      [:div.row 
       [:div.text-nowrap (map #(identity [:img {:height "100px" :src %}]) images)]])))

(defn create-submission-form []
  [:form
   [:div.form-group
    [:h6 "Title: "]
    [:input {:type "text" :placeholder "Spooky Title"}]]
   [:div.form-group
    [:h6 "Images: "]
    [:div {:style 
           {:height "100px" :background-color "#333333" :overflow "scroll-x"}} 
     [image-thumbnails]]]
   [:div.form-group
    [:label.btn.btn-primary.btn-file 
     [:i.fa.fa-superpowers {:style {:padding-right "5px"} }] 
     "Upload" 
     [:input.form-control-file 
      {:style {:display "none"} 
       :type "file" 
       ;;:on-change upload-queued
       }]]]])


(defn submit-image-modal []
  [:div
   (create-modal-header "Post Convo")
   (create-submission-form)])

(defn upload-page []
  [:div.container.jumbotron 
   [:div.page-header "Post Convo"] 
   [:div (create-submission-form)]])

