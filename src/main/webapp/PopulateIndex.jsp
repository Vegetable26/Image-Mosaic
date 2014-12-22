<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>

<head>

<script>
$("#theForm").submit(function(e) {
    var getData = $(this).serializeArray();
    var formURL = $(this).attr("action");
    $.ajax( {
        url : formURL,
        type: "GET",
        data : getData,
        success:function(data, textStatus, jqXHR){
            //data: return data from server
        },
        error: function(jqXHR, textStatus, errorThrown){
        //if fails
        }
    });
    e.preventDefault(); //STOP default action
    //e.unbind(); //unbind. to stop multiple form submit.
});

</script>


<script>
    var $body = $("body");

    $(document).ajaxStart( function() {
    $body.addClass("loading");
    });
    $(document).ajaxStop( function() {
    $body.removeClass("loading");
    });
</script>


</head>

<body>

<h3>Populate the index</h3>
<form id = "theForm" action="/crawl" method="GET">
<div>What should we search flickr for</div>
<div><input type="text" name="searchParam"></div>
<div>how many of these images should we pull</div>
<div><input type="text" name="howMany"></div>
<div><input id="submit" type="submit" value="crawl this"></div>
</form>

    <div class="modal"></div>


    </body>

</html>
