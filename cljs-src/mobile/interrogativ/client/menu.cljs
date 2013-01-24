(ns interrogativ.client.menu
  (:require [jayq.core :as jq])
  (:use [jayq.core :only [$]]))

;;;TODO translate to clojurescript, started at line 49
(js*
 "$(document).bind('pageinit', function() {
    $('#tilferdig').click(function() {
        var allChoices = $('div.name-holder').map(function() {return this.id;});
        var marked = $('select').map(function() {return this.name;});
        $.merge(marked, $('input:radio:checked').map(function() {return this.name;}));

        var unmarked = $.grep(allChoices, function(i, v) {return ($.inArray(i, marked) === -1);});

        var text = '<h2>Manglende svar.</h2>';
        //<p> ends in line with $('div[class=ikkeferdig]').replaceWith(...)
        text += '<p>De følgende spørsmålene er ikke besvarte. Trykk på spørsmålet ';
        text += 'for å svare. Om du ikke vil svare så kan du bla nederst på siden ';
        text += 'og trykke levér.';

        for(var i in unmarked) {
            var title = $('#'+unmarked[i]).attr('title');
            var html = $('#'+unmarked[i]).html();
            var body = '<p>'+title+'</p>';
            body += '<fieldset data-role=controlgroup data-mini=\"true\"';
            // if this is a [+ • −] choice we should put it horizontal
            if (html.indexOf('•') !== -1) {
                body += ' data-type=\"horizontal\">';
            } else {
                body += '>';
            }
            body += html+'</fieldset>';
            text+='<div data-role=\"collapsible\"><h4>'+title+'</h4>'+body+'</div>';
        }

        if (0 < unmarked.length) {
            // <p> ends here.
            $('div.ikkeferdig').replaceWith('<div class=\"ikkeferdig\">'+text+'</p>');
        }

        $('#div.ikkeferdig').trigger('create');
    });
    
    $('#tilbakeinnhold').click(function() {
        $('div.ikkeferdig > *').remove();
    });
});")

(comment
  (jq/bind ($ js/document) :pageinit (fn []
    (jq/bind ($ :#tilferdig) :click (fn []
      (let [unmarked (into #{} (map #(.-name %) ($ "input:radio:not(checked)")))
            text (str "<h2>Manglende svar.</h2>"
                      "<p>De følgende spørsmålene er ikke besvarte. Trykk på spørsmålet "
                      "for å svare. Om du ikke vil svare så kan du bla nederst på siden " 
                      "og trykke levér.")]
        (loop [unmarked (seq unmarked)
               text text]
          (if-let [um (first unmarked)]
            (let [title (jq/attr ($  (str "#" um)) :title)
                  html (.html ($ (str "#" um)))]
              (recur (rest unmarked)
                     (str text
                          "<div data-role='collapsible'><h4>"
                          "<p>" title "</p>"
                          "</h4>"
                          "<fieldset data-role=controlgroup data-mini=\"true\""
                          (if-not (= -1 (.indexOf html "•"))
                            " data-type=\"horizontal\">"
                            ">")
                          "</fieldset></div>")))
            (.replaceWith ($ :div.ikkeferdig)
                          (str "<div class='ikkeferdig'>"
                               text
                               "</p>"))))
        (trigger ($ :div.ikkeferdig) "create"))))

    (jq/bind ($ :#tilbakeinnhold) :click (fn []
      (jq/remove ($ "div.ikkeferdig > *")))))))

(defn live [$elem events handler]
  (.live $elem (jq/->event events) handler))

(live ($ :#ferdig) :pagebeforeshow (fn [event]
  (jq/remove ($ "#ferdig div[role=heading]"))))

(live ($ :#meny) :pagebeforeshow (fn [event]
  (let [$menyp ($ :#menyp)]
    (jq/inner $menyp
              (str "<h4>Du kan avslutte når som helst ved å trykke på denne knappen.</h4>"
                   "<a href=\"#\""
                   " id=\"avslutt\""
                   " data-role=\"button\""
                   " data-theme=\"c\">"
                   "Avslutt</a>" 
                   "<a href=\"#\""
                   " data-rel=\"back\""
                   " data-role=\"button\""
                   " data-theme=\"a\">"
                   "Tilbake</a>"))
    (jq/trigger $menyp "create")
    (jq/bind ($ :#avslutt) :click (fn []
      (jq/fade-out $menyp "fast" (fn []
        (jq/inner $menyp
                  (str "<h4>Er du sikker på at du vil avslutte?</h4>"
                       "<a href=\"#ferdig\" data-role=\"button\">Ja</a>"
                       "<a href=\"#\" data-rel=\"back\" data-role=\"button\">Nei</a>"))
        (jq/trigger $menyp :create)
        (jq/fade-in $menyp "slow"))))))))
