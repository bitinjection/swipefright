(ns swipefright.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [cljs-http.client :as http]
            [reagent-modals.modals :as modal] 
            [cljs.core.async :refer [<! >! chan put!]]
            [goog.string :as gstring]
            [cljsjs.react-transition-group :as transition] 
            [swipefright.url :as url]
            [swipefright.validation :as validation]) 
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def api-url "http://localhost:4000/api/")

(defonce app-state 
  (reagent/atom 
    {
     :content 
     {:body [:div "empty"]} 
     :menu-classes "navbar-collapse text-center collapse"
     :landing 
     { :notify-email "testerino!" 
      :notify-button-classes "btn btn-primary disabled" } 
     :uploaded-images []}))

(defn large-icon [name]
  [:div.col-12 [:i.fa.fa-4x {:class name}]])

(defn create-modal-header [title]
  [:div.modal-header.col-12
   [:h4.mx-auto title]
   [:button.close {:type "button" :data-dismiss "modal"}
    [:span (gstring/unescapeEntities "&times;")]]])

(defn create-modal-body [icon subject body]
  [:div.modal-body.text-center
   [:div.row
   (large-icon icon)
   [:div.col-12 [:h2 subject ]]]
   [body]])


(defn toggle-notify-button [activate]
  (if (true? activate)
    (swap! app-state assoc-in [:landing :notify-button-classes] "btn btn-primary")
    (swap! app-state assoc-in [:landing :notify-button-classes] "btn btn-primary disabled")))

(defn validate-email-input [e]
  (let [input (.-target.value e)]
    (swap! app-state assoc-in [:landing :notify-email] input)
    (toggle-notify-button (validation/is-valid-email? input))))


(defn subscribe-confirmed-modal []
  (letfn [(subscribed-body [] 
            [:div "Thanks!  We'll notify you when we're up and running!"])]
    [:div
     (create-modal-header "Thanks")
     (create-modal-body "fa-thumbs-up" "Subscribed" subscribed-body)]))



(defn save-email [email]
  (go (let [response 
            (<! (http/post 
                  (str api-url "emails")
                  {:with-credentials? false 
                   :json-params 
                   {:email {:email (get-in @app-state [:landing :notify-email])}}}))]
        (modal/modal! (subscribe-confirmed-modal)))))

(defn validate-email-on-enter [event]
  (let [code (.-charCode event)]
    (if (= code 13)
      (validate-email-input event))))

(defn notify-body []
  (let [button-classes (get-in @app-state [:landing :notify-button-classes])]
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
           :on-key-press validate-email-on-enter 
           :on-change validate-email-input}]]
        [:div.form-group 
         [:a 
          {:class 
           (get-in @app-state [:landing :notify-button-classes])
           :on-click save-email 
           :href "#" }
          "Notify Me" ]]]])))

(defn subscribe-modal []
  [:div
   (create-modal-header "Summoning")
   (create-modal-body "fa-newspaper-o" "Summoning" (notify-body))])

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
        (swap! app-state update-in [:uploaded-images] conj (.-target.result e)))) 
    (.readAsDataURL reader file)))

(extend-type js/FileList
  ISeqable
  (-seq [array] (array-seq array 0)))

(defn upload-queued [e]
  (image-link (first e.target.files)))

(defn image-thumbnails []
  (fn []
    (let [images (get-in @app-state [:uploaded-images])]
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
       :on-change upload-queued}]]]])

(defn submit-image-modal []
  [:div
   (create-modal-header "Post Convo")
   (create-submission-form)])

(defn upload-page []
  [:div.container.jumbotron 
   [:div.page-header "Post Convo"] 
   [:div (create-submission-form)]])

(defn jumbotron []
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
        {:on-click #(modal/modal! (subscribe-modal)) :href "#"}
        [:i.fa.fa-superpowers {:style {:padding-right "5px"}}]
        "Enter"]]]]]])

(defn format-post [info]
  [:div.container.text-center
   [:div.post-title
    [:h3 (:title info)]
    [:h6 (str  (:caption info) "!?")]]
   [:div
    [:img.text-message-image
     {:src (str "images/posts/" (:image (first (:images info))))}]]])

(defn parse-post [json]
  (let [parsed (get-in json [:body :data])]
    (format-post  {:title (:title parsed)
                   :caption (:caption parsed)
                   :images (:images parsed)})))

(defn fetch-post [id]
  (go 
    (let [response (<! (http/get (str api-url "posts/" (url/decode52 id))
                                   {:with-credentials? false}))
          json (js->clj response)]
      (if (not (:success json))
        (swap!  app-state assoc-in [:jumbotron] 
          (str "Unable to fetch post. " response)) 
        (swap! app-state assoc-in [:jumbotron] 
               (parse-post json)))))
  nil)

(defn random-post []
  (go (let [total (get-in (<! (http/get 
                          (str api-url "posts/count") {:with-credentials? false} ))
                          [:body :data :total])]
        (fetch-post (url/encode52 (rand-int total)))))
  nil)

(defn submit-button []
  [:li {:on-click #(random-post) }
   [:a.btn.btn-secondary
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
         :on-click #(random-post)
         } 
        [:i.padded-icon.fa.fa-random]
        "Random"]]]]
    [:a.navbar-brand.mx-auto.w-100.text-center 
     [:img.img-fluid {:src "images/sflogov2.svg"}]]
    [right-button]]
   (get @app-state :jumbotron)
   [:footer.footer.text-center
    [:div.container
     [:div.text-muted
      "This site should not be viewed by users with a history of heart problems."]]]]) 

(defn about-page []
  [:div [:h1 "About swipefright"]
   [:div {:dude "whoa" } [:a {:href "/" :role "button"} "go to the home page"]]])

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]
  [modal/modal-window]])

;; -------------------------
;; Routes



(secretary/defroute "/" []
  (reset! page #'home-page))

;;(swap! app-state assoc :jumbotron (fetch-post "E"))

;; Routes like these need to be setup in on the backend
(secretary/defroute "/p/:id" [id]
  (fetch-post id))

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
