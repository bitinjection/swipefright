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
            [swipefright.validation :as validation]
            [swipefright.site.core :as site]
            ) 
  (:import
    [goog.history Html5History EventType]
    [goog Uri]
    )
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def api-url "http://localhost:4000/api/")

(defonce app-state 
  (reagent/atom 
    {
     :jumbotron :jumbotron
     :post {:title "Loading..." :caption nil :images nil :class "post-loading"}
     :content 
     {:body [:div "empty"]} 
     :menu-classes "navbar-collapse text-center collapse"
     :landing 
     { :notify-email "testerino!" 
      :notify-button-classes "btn btn-primary disabled" } 
     :uploaded-images []}))

(add-watch app-state :post (fn [a b c d] (pr "post updated")))
(add-watch app-state :jumbotron (fn [a b c d] (pr "jumbotron updated")))


(defn toggle-notify-button [activate]
  (if (true? activate)
    (swap! app-state assoc-in [:landing :notify-button-classes] "btn btn-primary")
    (swap! app-state assoc-in [:landing :notify-button-classes] "btn btn-primary disabled")))

(defn validate-email-input [e]
  (let [input (.-target.value e)]
    (swap! app-state assoc-in [:landing :notify-email] input)
    (toggle-notify-button (validation/is-valid-email? input))))

(defn save-email [email]
  (go (let [response 
            (<! (http/post 
                  (str api-url "emails")
                  {:with-credentials? false 
                   :json-params 
                   {:email {:email (get-in @app-state [:landing :notify-email])}}}))]
        (modal/modal! (site/subscribe-confirmed-modal)))))

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
        (swap! app-state update-in [:uploaded-images] conj (.-target.result e)))) 
    (.readAsDataURL reader file)))

(extend-type js/FileList
  ISeqable
  (-seq [array] (array-seq array 0)))

(defn upload-queued [e]
  (image-link (first e.target.files)))

(defn format-post []
  (let [post-info (get @app-state :post)]
    (pr "updating html")
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
        :class (:class post-info)}]]]))

(defn post-update [current json]
  (-> current
      (assoc :post
             {:title (:title json)
              :caption (:caption json)
              :images (:images json)
              :class "post-image"})
      (assoc :jumbotron :post)))

(defn parse-post [json]
  (let [parsed (get-in json [:body :data])]
    (swap! app-state post-update parsed)))

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
        (accountant/navigate! (str "/p/" (url/encode52 id)))
        ))
  (swap! app-state
               assoc
               :post
               {:title nil :caption nil :images [{ :image "loading.gif"}] :class "post-loading"}))

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
     [:img.img-fluid 
      {:on-click #(swap! app-state assoc :jumbotron :jumbotron)
       :src "/images/sflogov2.svg"}]]
    [right-button]]
   (if (= :jumbotron (get @app-state :jumbotron))
     (site/jumbotron random-post)
     [format-post])
   
   [:footer.footer.text-center
    [:div.container
     [:div.text-muted.footer-text
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

(def history (Html5History.))
(.setUseFragment history false)
(.setPathPrefix history "")
(.setEnabled history true)

(defn nav! [token]
  (. history (setToken "swipefright" token)))

(secretary/defroute "/" []
  (reset! page #'home-page))

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
