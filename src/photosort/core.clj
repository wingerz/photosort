(ns photosort.core
  (:import [java.io File]
           [org.apache.sanselan ImageReadException Sanselan]
           [org.apache.sanselan.common IImageMetadata, RationalNumber]
           [org.apache.sanselan.formats.jpeg JpegImageMetadata]
           [org.apache.sanselan.formats.tiff.constants ExifTagConstants])
  (:gen-class))
 

(defn get-image-filenames
  [dir]
    (filter (fn [x] (re-find #".jpg$" x))
          (for [file (file-seq (File. dir))] (str (.getPath file)))))
  
(defn get-tag-value [metadata tagInfo]
  (if-let [field (.findEXIFValue  metadata tagInfo)]
    (.getValueDescription field)
    nil))


(defn readexif [file]
  (let [metadata (Sanselan/getMetadata file)]
    (when (nil? metadata)
      nil)
    (when (instance? JpegImageMetadata metadata)
      (get-tag-value metadata (ExifTagConstants/EXIF_TAG_DATE_TIME_ORIGINAL))
      )))

(defn get-output [dir dest-dir] 
  (let [files (get-image-filenames dir)
        files-by-date (sort #(compare (last %1) (last %2))
                            (map
                             (fn [x]
                               (vec [x (readexif (File. x))]) )
                             files))
        num-files (count files)]
     (for [i (range num-files)]
       (format "cp \"%s\% \"%s/%03d.jpg\"" (first (nth files-by-date  i)) dest-dir (inc i)))))



(defn -main [& args]
  (println (get-output (first args) (second args))))

    
(-main "/Volumes/LaCie/temp/consumer" "/Volumes/Lacie/temp/consumer-by-date" )