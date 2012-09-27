/*
 *  Javascript to fix the last page and add missing questions.
 *
 */

$(document).bind("pageinit", function() {
    $("#tilferdig").click(function() {

        var allChoices = $("div[class=name-holder]").map(function() {return this.id;});

        var marked = $("select").map(function() {return this.name;});
        $.merge(marked, $("input:radio:checked").map(function() {return this.name;}));

        var unmarked = $.grep(allChoices, function(i, v) {return ($.inArray(i, marked) === -1);});

        var text = "<h2>Manglende spørsmål.</h2>";
        //<p> ends in line with $("div[class=ikkeferdig]").replaceWith(...)
        text += "<p>De følgende spørsmålene er ikke besvarte. Trykk på spørsmålet ";
        text += "for å svare. Om du ikke vil svare så kan du bla nederst på siden ";
        text += "og trykke levér.";

        for(var i in unmarked) {
            var title = $("div[id="+unmarked[i]+"][class=name-holder]").attr("title");
            var html = $("div[id="+unmarked[i]+"][class=name-holder]").html();
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

        $("#ferdig").trigger("pagecreate");
    });
    
    $("#tilbakeinnhold").click(function() {
        $("div.ikkeferdig > *").remove();
    });
});

$(document).delegate("#ferdig", "pagebeforeshow", function() {
    $("#ferdig div[role=heading]").remove();
});
