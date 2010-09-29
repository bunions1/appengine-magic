(ns appengine-magic.services.urlfetch
  (:import [com.google.appengine.api.urlfetch URLFetchServiceFactory]))

(defonce *urlfetch-service* (atom nil))

(defn- get-urlfetch-service []
  (do (when (nil? @*urlfetch-service*)
	(reset! *urlfetch-service* (URLFetchServiceFactory/getURLFetchService)))
      @*urlfetch-service*))

(defn- urlify [url]
  (if (string? url) (java.net.URL. url) url))

(defn fetch [url]
  (.fetch (get-urlfetch-service) (urlify url)))

(defrecord HTTPResponse [content
			 final-url
			 headers
			 response-code])

(defn- parse-headers [headers]
  (zipmap (map #(keyword (.getName %)) headers)
	  (map #(.getValue %) headers)))

(defn- parse-response
  [^com.google.appengine.api.urlfetch.HTTPResponse r]
  (HTTPResponse. (.getContent r)
		 (.getFinalUrl r)
		 (parse-headers (.getHeaders r))
		 (.getResponseCode r)))

(defn fetch-async [url]
  (let [f (.fetchAsync (urlify url))]
    (reify
     clojure.lang.IDeref
      (deref [_] (parse-response (.get f)))
     java.util.concurrent.Future
      (get [_] (.get f))
      (get [_ timeout unit] (.get f timeout unit))
      (isCancelled [_] (.isCancelled f))
      (isDone [_] (.isDone f))
      (cancel [_ interrupt?] (.cancel f interrupt?)))))
