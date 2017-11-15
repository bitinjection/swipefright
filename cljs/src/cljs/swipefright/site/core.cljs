(ns swipefright.site.core
  (:require [reagent-modals.modals :as modal]
            [swipefright.controllers.index :as controllers]
            [reagent.session :as session]
            [goog.string :as gstring]))

(defn submit-button []
  [:li 
   [:a.btn.btn-secondary.disabled
    [:i.fa.fa-cloud-upload.padded-icon ]
    "Submit"]])

(defn nav-menus []
  [:ul.nav.navbar-nav.navbar-right
   ;;(menu-item "Random" "fa-random")
   (submit-button)])

(defn toggle-class [a k class1 class2]
  (if (= (@a k) class1)
    (swap! a assoc k class2)
    (swap! a assoc k class1)))

(defn right-button []
  (fn []
    [:div {:class (get @app-state :menu-classes)} 
     [:ul.nav.navbar-nav.ml-auto
      (submit-button)]]))

(defn about-page []
  [:div [:h1 "About swipefright"]
   [:div {:dude "whoa" } [:a {:href "/" :role "button"} "go to the home page"]]])

(defn home-page []
  [:div
   [:nav.navbar.navbar-expand-sm.navbar-dark {:id "topNav"}
    [:button.navbar-toggler.navbar-toggler-right
     {:type "button", :data-toggle "collapse", :data-target ".navbar-collapse" }
     [:i.fa.fa-bars]]
    [:div.navbar-collapse.collapse 
     [:ul.nav.navbar-nav
      [:li.text-center 
       [:a.btn.btn-secondary
        {:href "#"
         :on-click #(controllers/random-post)
         } 
        [:i.padded-icon.fa.fa-random]
        "Random"]]]]
    [:a.navbar-brand.mx-auto.w-100.text-center 
     [:img.img-fluid 
      {:on-click #(session/swap! assoc :jumbotron :jumbotron)
       :src "/images/sflogov2.svg"}]]
    [right-button]]
   (if (= :jumbotron (session/get :jumbotron))
     (site/jumbotron controllers/random-post)
     [format-post])


   [:footer.footer.text-center
    [:div.container
     [:div.text-muted.footer-text
      "This site should not be viewed by users with a history of heart problems."]]]]) 

(defn toggle-class [a k class1 class2]
  (if (= (@a k) class1)
    (swap! a assoc k class2)
    (swap! a assoc k class1)))
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
  (let [button-classes (session/get-in [:landing :notify-button-classes])]
  ;;(let [button-classes nil]
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
          {:class 
           (session/get-in [:landing :notify-button-classes])
           :on-click save-email 
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
    (let [images (session/get-in [:uploaded-images])]
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

(defn toggle-notify-button [activate]
  (if (true? activate)
    (session/swap! assoc-in [:landing :notify-button-classes] "btn btn-primary")
    (session/swap!
      assoc-in
      [:landing :notify-button-classes]
      "btn btn-primary disabled")))
