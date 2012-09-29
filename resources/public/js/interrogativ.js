/*
 *  Javascript to fix the last page and add missing questions.
 *
 */

$(document).bind("pageinit", function() {
    $("#tilferdig").click(function() {
        var allChoices = $("div.name-holder").map(function() {return this.id;});
        var marked = $("select").map(function() {return this.name;});
        $.merge(marked, $("input:radio:checked").map(function() {return this.name;}));

        var unmarked = $.grep(allChoices, function(i, v) {return ($.inArray(i, marked) === -1);});

        var text = "<h2>Manglende svar.</h2>";
        //<p> ends in line with $("div[class=ikkeferdig]").replaceWith(...)
        text += "<p>De følgende spørsmålene er ikke besvarte. Trykk på spørsmålet ";
        text += "for å svare. Om du ikke vil svare så kan du bla nederst på siden ";
        text += "og trykke levér.";

        for(var i in unmarked) {
            var title = $("#"+unmarked[i]).attr("title");
            var html = $("#"+unmarked[i]).html();
            var body = "<p>"+title+"</p>";
            body += "<fieldset data-role=controlgroup data-mini=\"true\"";
            // if this is a [+ • −] choice we should put it horizontal
            if (html.indexOf("•") !== -1) {
                body += " data-type=\"horizontal\">";
            } else {
                body += ">";
            }
            body += html+"</fieldset>";
            text+="<div data-role='collapsible'><h4>"+title+"</h4>"+body+"</div>";
        }

        if (0 < unmarked.length) {
            // <p> ends here.
            $("div.ikkeferdig").replaceWith("<div class='ikkeferdig'>"+text+"</p>");
        }

        $("#div.ikkeferdig").trigger("create");
    });
    
    $("#tilbakeinnhold").click(function() {
        $("div.ikkeferdig > *").remove();
    });
});

function avslutt() {
    var html  =  "<h4>Er du sikker på at du vil avslutte?</h4>";
        html += "<a href=\"#ferdig\" data-role=\"button\">Ja</a>";
        html += "<a href=\"#\" data-rel=\"back\" data-role=\"button\">Nei</a>";
    $("#menyp").fadeOut("fast", function() {
        $("#menyp").html(html);
        $("#menyp").trigger("create");
        $("#menyp").fadeIn("slow");
    });
}

$(document).delegate("#ferdig", "pagebeforeshow", function() {
    $("#ferdig div[role=heading]").remove();
});

$(document).delegate("#meny", "pagebeforeshow", function() {
    var html  = "<h4>Du kan avslutte når som helst ved å trykke på denne knappen.</h4>";
        html += "<a href=\"#\""
                + " id=\"avslutt\""
                + " data-role=\"button\""
                + " data-theme=\"c\""
                + " onclick=\"avslutt()\">"
                + "Avslutt</a>";
        html += "<a href=\"#\""
                + " data-rel=\"back\""
                + " data-role=\"button\""
                + " data-theme=\"a\">"
                + "Tilbake</a>";
    $("#menyp").html(html);
    $("#menyp").trigger("create");
});
