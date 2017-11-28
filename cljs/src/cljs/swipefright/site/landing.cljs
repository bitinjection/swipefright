(ns swipefright.site.landing)

(defn jumbotron [enter]
  [:div.container 
   [:div.jumbotron.content-background.mt-5
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
