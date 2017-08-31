(ns conduit.components.header
  (:require [rum.core :as rum]))

(def nav-items
  [{:label "Home"
    :route :home
    :link "/"}
   {:label "New Post"
    :route :new-post
    :icon "ion-compose"
    :link "/new-post"}
   {:label "Settings"
    :route :settings
    :icon "ion-gear-a"
    :link "/settings"}
   {:label "Sign in"
    :route :login
    :link "/login"}
   {:label "Sign up"
    :route :sign-up
    :link "/register"}])

(rum/defc NavItem [curr-route {:keys [label icon route link]}]
  [:li.nav-item {:class (when (= route curr-route) "active")}
   [:a.nav-link {:href (str "#" link)}
    (when icon [:i {:class icon}])
    (when icon " ")
    label]])

(rum/defc Header [r route current-user]
  (let [user-nav-items (if current-user
                         (->> {:label (str "Hi, " (:username current-user))
                               :route :user
                               :link (str "/users/" (:username current-user))}
                              (conj (filter #(contains? #{:home :new-post :settings} (:route %)) nav-items)))
                         (filter #(contains? #{:home :login :sign-up} (:route %)) nav-items))]
    [:nav.navbar.navbar-light
     [:div.container
      [:a.navbar-brand {:href "#/"} "conduit"]
      [:ul.nav.navbar-nav.pull-xs-right
       (map #(rum/with-key (NavItem route %) (:label %)) user-nav-items)]]]))
