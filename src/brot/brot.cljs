(ns brot.brot)

(defn z+ [[xa yai]
          [xb ybi]]
  [(+ xa xb)
   (+ yai ybi)])

(defn z-sqr [[x y]]
  [(- (* x x)
      (* y y))
   (* x y 2)])

(defn z-abs [[x y]]
  (.sqrt js/Math (+ (* x x) (* y y))))

(defn mandelbrot
  [c z]
  (z+ (z-sqr z) c))

(defn recursions
  [c max-iterations]
  (loop [z [0 0] iterations 0]
    (let [r (mandelbrot c z)]
      (if (or (>= iterations max-iterations)
              (> (z-abs r) 2))
        iterations
        (recur r (inc iterations))))))
