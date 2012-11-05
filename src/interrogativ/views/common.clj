(ns interrogativ.views.common
  (:require [clojure.string :only [replace lower-case] :as string])
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-js include-css]]
        [hiccup.core :only [html]])
  (:refer-clojure :exclude [name id]))

(defpartial body [& content]
  [:body content])

(def jquery "http://code.jquery.com/")

(defpartial layout [{:keys [title body]}]
  [:head
   [:title title]
   [:meta
    (include-css (str jquery "ui/1.9.1/themes/base/jquery-ui.css"))
    (include-js (str jquery "jquery-1.8.2.js"))
    (include-js (str jquery "ui/1.9.1/jquery-ui.js"))
    (include-js "https://github.com/blueimp/jQuery-File-Upload/blob/33054ac2c757277192b34f10bf33f23fef3242be/js/jquery.fileupload.js")
    (include-js "https://github.com/cmlenz/jquery-iframe-transport/blob/22bf3d05f979811827e5c8c001098d24d9c801b2/jquery.iframe-transport.js")
    [:script
  "$(function() {
     $(\"#accordion\").accordion({ heightStyle: \"content\",
                                   collapsible: \"true\",
                                   active: \"false\" })
//     $(\"#accordion\").addClass(\"ui-accordion ui-widget ui-helper-reset\")
//  .find('h3')
//  .addClass(\"current ui-accordion-header ui-helper-reset ui-state-active ui-corner-top\")
//  .prepend('<span class=\"ui-icon ui-icon-triangle-1-s\"/>')
//  .next().addClass(\"ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active\");
   });
   
   $(function() {
     $(\"input[type=submit], input[type=file], a.csv, button\").button();
     $(\"a.link\").button({ icons: { primary: \"ui-icon-link\"}});
     $(\"a.file\").button({ icons: { primary: \"ui-icon-document\"}});
   });"]]]
  body)

