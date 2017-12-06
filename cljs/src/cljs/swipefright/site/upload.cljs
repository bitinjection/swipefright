(ns swipefright.site.upload
  (:require [reagent.session :as session]
            [cats.core :as m]
            [cats.monad.maybe :as maybe]
            [reagent-modals.modals :as modal]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! >! chan put!]]
            [reagent.core :as r :refer [atom]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn text-input [label placeholder required on-changed]
  (fn [label placeholder required on-changed]
    [:div.row.form-group
     [:label.control-label {:class (if required "required")} label]
     [:input.form-control.form-control-warning
      {:type "text"
       :placeholder placeholder
       :on-change #(on-changed (-> % .-target .-value))}]]))

(defn load-image [image inputs]
  (let [reader (js/FileReader.)]
    (aset reader "onload" #(swap! inputs assoc :image (-> % .-target .-result)))
    (.readAsDataURL reader image))) 

(defn recaptcha []
  (r/create-class
    {:display-name "recaptcha"
     :reagent-render (fn [] [:div.g-recaptcha {:data-sitekey "6LcPszoUAAAAAO1xLORatfVrSUVPvpgFpPxdiv7H"}])}))

(defn post-image [data] 
  (go
    (let [ request {"pending_post" @data}
          response (<! (http/post
                         "https://swipefright.org:8443/api/pending"
                         {:with-credentials? false
                         :json-params request}))]
      (if (= (:status response) 201)
        (session/swap! assoc :upload-page :upload-success)
        (session/swap! assoc :upload-page :upload-failure)))))

(defn update-inputs [inputs k]
  (partial swap! inputs assoc k))

(defn empty-image [inputs]
  [:label.btn.btn-secondary.btn-file.image-upload-button
   {:on-change #(load-image (-> % .-target .-files first) inputs) :style {:width "100%" :min-height "100%"}}
   [:i.fa.fa-upload.col-12.text-center.mt-5]
   [:input.form-control-file 
    {:style {:display "none"}
     :type "file"}]]) 

(defn rendered-image [inputs]
  (fn [inputs]
    [:img.uploaded-image.text-center.mx-auto 
     {:src (:image @inputs)
      :on-click #(swap! inputs assoc :image nil)}]))

(defn image-previewer [inputs]
  (fn [inputs]
    [:div.form-group
     [:label.text-right.control-label.required "Image"]
     [:div.align-items-center.image-upload-area
      (if (nil? (:image @inputs))
        [empty-image inputs]
        [rendered-image inputs])]]))

(defn validate-upload [data]
  (= false (or (nil? (:title data)) (nil? (:image data)))))

(defn upload-button [process inputs]
  (fn [process]
    [:a.btn.btn-primary.btn-file
     {:href "#" :on-click #(process)
      :class (if (validate-upload @inputs) "" "disabled")
      }
     [:i.fa.fa-superpowers {:style {:padding-right "5px"} }] 
     "Upload"]))

(defn create-submission-form [page] 
  (let [inputs (r/atom {:title nil :caption nil :image nil :valid "disabled"})]
    [:form.mt-5.upload-form.content-background.p-5
     [:div.form-group
      [text-input "Title" "Title of spooky post" true (update-inputs inputs :title)]
      [text-input "Caption" "Frightenly snarky caption" false (update-inputs inputs :caption)]]
     [image-previewer inputs]
     [:div.col-12.mt-3.row
      [:div.mx-auto
       [recaptcha]
       [upload-button (partial post-image inputs) inputs]]]]))

(defn upload-success []
  [:div "Upload success!"])

(defn upload-failure []
  [:div.mt-5.content-background [:h2.text-center "Error"]
         "Unable to process upload."])

(defn main-page []
  [:div.col-xs-12.col-lg-5.container
   (condp = (session/get :upload-page)
         :upload [create-submission-form]
         :upload-success [upload-success]
         :upload-failure [upload-failure])])
