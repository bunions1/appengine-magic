(ns appengine-magic.services.blobstore
  (:import [com.google.appengine.api.blobstore ByteRange BlobKey
            BlobstoreService BlobstoreServiceFactory]
           [javax.servlet.http HttpServletRequest HttpServletResponse]))


(defonce *blobstore-service* (atom nil))


(defn get-blobstore-service []
  (when (nil? @*blobstore-service*)
    (reset! *blobstore-service* (BlobstoreServiceFactory/getBlobstoreService)))
  @*blobstore-service*)


(defn upload-url [success-path]
  (.createUploadUrl (get-blobstore-service) success-path))


(defn delete! [& args]
  ;; TODO: Implement this.
  )


(defn fetch-data [^:BlobKey blob-key, start-index, end-index]
  (.fetchData (get-blobstore-service) blob-key start-index end-index))


(defn byte-range [^:HttpServletRequest request]
  (.getByteRange (get-blobstore-service) request))


(defn uploaded-blobs [^:HttpServletRequest request]
  (into {} (.getUploadedBlobs (get-blobstore-service) request)))


(defn- make-blob-key [x]
  (if (instance? BlobKey x)
      x
      (BlobKey. x)))


(defn serve
  ([blob-key, ^:HttpServletResponse response]
     (.serve (get-blobstore-service) (make-blob-key blob-key) response))
  ([blob-key, start, end, ^:HttpServletResponse response]
     (.serve (get-blobstore-service) (make-blob-key blob-key) (ByteRange. start end) response)))