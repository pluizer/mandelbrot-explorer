(ns brot.core
  (:require [reagent.core :as reagent :refer [atom dom-node]]
            [brot.brot :refer [recursions]]))

(defonce state (atom {:area [[-2 2] [-2 2]] :zoom 1 :iterations 50}))

(defn put-pixel [context [x y] [r g b]]
  "Render a pixel with a spefic colour to a canvas."
  (set! (.-fillStyle context) (str "rgb(" r "," g "," b ")"))
  (.fillRect context x y 1 1))

(defn render [canvas w h]
  (let [context           (.getContext canvas "2d")
        [[fx tx] [fy ty]] (:area @state)
        [sx sy]           [(- tx fx) (- ty fy)]
        size              (max sx sy)
        iterations        (:iterations @state)]
    (set! (.-fillStyle context) "black")
    (doall (for [i (range (* w h))]
             (let [x (mod i w)
                   y (.floor js/Math (/ i w))]
               (let [c (recursions [(+ (* (/ size w) x) fx)
                                    (+ (* (/ size h) y) fy)]
                                   iterations)
                     color (if (< c iterations)
                             [(* c (/ 256 iterations)) 0 0]
                             [0 0 0])]
                 (put-pixel context [x y] color)))))))

(defn canvas-position [event w h]
  "Map the position of the mouse event on the canvas to coordinates in complex plane"
  (let [canvas (.-target event)
        rect (.getBoundingClientRect canvas)]
    (let [[[fx tx] [fy ty]] (:area @state)
          [sx sy]           [(- tx fx) (- ty fy)]]
      [(+ fx (* sx (/ (- (.-clientX event) (.-left rect)) w)))
       (+ fy (* sy (/ (- (.-clientY event) (.-top  rect)) h)))])))

(defn canvas [w h]
  [:div [:canvas {:width w :height h
                  :on-click   #(let [z     (:zoom @state)
                                     f     (* 2 z)
                                     [x y] (canvas-position % w h)]
                                 (swap! state assoc
                                        :zoom (* z .7)
                                        :area [[(- x f) (+ x f)] [(- y f) (+ y f)]]))}]])

(defn canvas-with-callback [w h]
  (let [render (fn [e]
                 (let [canvas (.-firstElementChild (dom-node e))]
                   (render canvas w h)))]
    (with-meta #(canvas w h) {:component-did-mount render
                              :component-did-update render})))

(defn home-page []
  (let [iterations (:iterations @state)]
    [:div [(canvas-with-callback 200 200)]
     [:input {:type "range" :min "5" :max "255" :value iterations
              :on-change #(swap! state assoc :iterations (-> % .-target .-value))}]
     [:div (str iterations " iterations")]]))

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))

