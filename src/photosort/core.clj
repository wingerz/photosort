(ns photosort.core
  (:import [java.io File]
           [org.apache.sanselan ImageReadException Sanselan]
           [org.apache.sanselan.common IImageMetadata, RationalNumber]
           [org.apache.sanselan.formats.jpeg JpegImageMetadata]
           [org.apache.sanselan.formats.tiff TiffField TiffImageMetadata]
           [org.apache.sanselan.formats.tiff.constants ExifTagConstants
            GPSTagConstants TagInfo TiffConstants TiffTagConstants])
  (:gen-class))
 
 
(defn print-tag-value [metadata tagInfo]
  (if-let [field (.findEXIFValue  metadata tagInfo)]
    (println (str "        (" (.name tagInfo) ": " (.getValueDescription field)))
    (println (str "        (" (.name tagInfo) " not found.)"))))
 
(defn readexif [file]
  (let [metadata (Sanselan/getMetadata file)]
    (when (nil? metadata)
      (println "\tNo EXIF metdata was found"))
    (when (instance? JpegImageMetadata metadata)
      (println "  -- Standard EXIF Tags")
      (print-tag-value metadata (TiffTagConstants/TIFF_TAG_XRESOLUTION))
      (print-tag-value metadata (TiffTagConstants/TIFF_TAG_DATE_TIME))
      (print-tag-value metadata (ExifTagConstants/EXIF_TAG_DATE_TIME_ORIGINAL))
      (print-tag-value metadata (ExifTagConstants/EXIF_TAG_CREATE_DATE))
      (print-tag-value metadata (ExifTagConstants/EXIF_TAG_ISO))
      (print-tag-value metadata (ExifTagConstants/EXIF_TAG_SHUTTER_SPEED_VALUE))
      (print-tag-value metadata (ExifTagConstants/EXIF_TAG_APERTURE_VALUE))
      (print-tag-value metadata (ExifTagConstants/EXIF_TAG_BRIGHTNESS_VALUE))
      (print-tag-value metadata (GPSTagConstants/GPS_TAG_GPS_LATITUDE_REF))
      (print-tag-value metadata (GPSTagConstants/GPS_TAG_GPS_LATITUDE))
      (print-tag-value metadata (GPSTagConstants/GPS_TAG_GPS_LONGITUDE_REF))
      (print-tag-value metadata (GPSTagConstants/GPS_TAG_GPS_LONGITUDE_REF))
 
      ; simple interface to GPS data
      (println "  -- GPS Info")
      (when-let [exifMetadata (.getExif metadata)]
        (when-let [gpsInfo (.getGPS exifMetadata)]
          (let [longitude (.getLongitudeAsDegreesEast gpsInfo)
                latitude (.getLatitudeAsDegreesNorth gpsInfo)]
            (println (str "        GPS Description: " gpsInfo))
            (println (str "        GPS Longitude (Degrees East):" longitude))
            (println (str "        GPS Latitude (Degrees North):" latitude)))))
 
      ; Print all EXIF data
      (println "  -- All EXIF info")
      (doseq [item (.getItems metadata)]
        (println (str "        " item)))
      (println))))
 
(defn -main []
  (let [file (File. "images/haxor.jpg")]
    (prn 'file (.getPath file))
    (readexif file)))