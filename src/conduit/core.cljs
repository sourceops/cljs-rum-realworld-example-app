(ns conduit.core
  (:require [rum.core :as rum]
            [scrum.core :as scrum]
            [goog.dom :as dom]
            [conduit.controllers.router :as router]
            [conduit.effects :as effects]
            [conduit.controllers.articles :as articles]
            [conduit.controllers.tags :as tags]
            [conduit.controllers.tag-articles :as tag-articles]
            [conduit.components.router :refer [Router]]
            [conduit.components.home :refer [Home]]))

(def routes
  ["/" [["" :home]
        [["tag/" :id] :home]]])

;; create Reconciler instance
(defonce reconciler
  (scrum/reconciler
    {:state (atom {})
     :controllers
     {:router router/control
      :articles articles/control
      :tag-articles tag-articles/control
      :tags tags/control}
     :effect-handlers {:http effects/http}}))

;; initialize controllers
(defonce init-ctrl (scrum/broadcast-sync! reconciler :init))

(defn on-navigate [r {:keys [handler route-params]}]
  (let [{:keys [id]} route-params]
    (cond
      (and (= handler :home) id)
      (do
        (scrum/dispatch! r :tag-articles :load id)
        (scrum/dispatch! r :tags :load))

      (= handler :home)
      (do
        (scrum/dispatch! r :tag-articles :reset)
        (scrum/dispatch! r :articles :load)
        (scrum/dispatch! r :tags :load)))))

(rum/mount (Router reconciler routes {:home Home} on-navigate)
           (dom/getElement "app"))

