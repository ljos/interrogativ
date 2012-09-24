/*
 *  Javascript to fix the last page and add missing questions.
 * 
 */

$(document).ready(function() {
    $("a[href=#ferdig]").click(function() {
        var allChoices = $("div[class=name-holder]").map(function() {return this.id;});
        
        var marked = $("select").map(function() {return this.name;});
        $.merge(marked, $("input:radio:checked").map(function() {
            return this.name;
        }));

        var unmarked = $.grep(allChoices, function(i, v) {return ($.inArray(i, marked) === -1);});
        
        //<p> ends in line with $("div[class=ikkeferdig]").replaceWith(...)
        var text = "<h3>Manglende spørsmål.</h3>"+
                "<p>De følgende spørsmålene er ikke besvarte. Trykk på spørsmålet "+
            "for å svare. Om du ikke vil svare så kan du bla nederst på siden "+
            "og trykke levér.";

        $("legend").remove();

        for(var i in unmarked) {
            var title = $("div[id="+unmarked[i]+"][class=name-holder]").attr("title");
            var html = $("div[id="+unmarked[i]+"][class=name-holder]").html();
            var body = "<p>"+title+"</p>"+
                    "<fieldset data-role=controlgroup>"+
                    html+
                    "</fieldset>";
            
            text+="<div data-role='collapsible'><h4>"+title+"</h4>"+body+"</div>";
        }

        
        if (0 < unmarked.length) {
            // <p> ends here.
            $("div[class=ikkeferdig]").replaceWith("<div class='ikkeferdig'>"+text+"</p>");
        }
        $("#ferdig").trigger("pagecreate");
    });
});
