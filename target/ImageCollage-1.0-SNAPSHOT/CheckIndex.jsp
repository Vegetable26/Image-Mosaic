<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<<<<<<< HEAD


<html>
<head>
    <script type="text/javascript">
    function getSearches(){
        $.get('crawl', {get : 'crawlerSearches'}, function(responseJson) { //get the crawlerSearches using the crawler servlet
            $.each(responseJson, function(index, crawlerSearch) {  //for each CrawlerSearch object...
                $('<tr>').appendTo('#crawlerSearches')  //make a new row and add it to the table
                .append($('<td class="time">').text(crawlerSearch.time))  //add the time cell
                .append($('<td>').text(crawlerSearch.searchParam))  //add the searchParam cell
                .append($('<td>').text(crawlerSearch.numImg));  //add the numImg cell
            });
        });
    }
    getSearches();
    </script>

    <script>
    $(document).ready(function() {
        $('#crawlerSearches').on('click', 'tr', function() { //when you click a row in the table
            document.getElementById("deleteImgs").innerHTML = "<br><i>Check the images that you want to delete.</i><br>";
            var time = $(this).find('td.time').html();
            $.get('crawl?get='+time, function(responseJson) {  //get the images since time using crawler servlet
                $.each(responseJson, function(index, item) {  //for each image since time...
                    var split = item.split(" ");
                    var $img = $('<img>').attr('src', split[0]);  //get the url
                    var $input = $('<input>');  //make a new input for the form
                    $input.attr('type', 'checkbox');
                    $input.attr('name', 'deleteMe');
                    $input.attr('value', item);
                    $input.appendTo('#deleteImgs');
                    $img.appendTo('#deleteImgs');
                    $('#deleteImgs').append('<br>');
                });
                var $submit = $('<input>');  //make the submit button
                $submit.attr('type', 'submit');
                $submit.attr('value', 'Delete checked images');
                $submit.appendTo('#deleteImgs');
            });
        });
    });
    </script>

    <script>
    $("#deleteImgs").submit(function(e) {
        var getData = $(this).serializeArray();
        var formURL = $(this).attr("action");
        $.ajax( {
            url : formURL,
            type: "GET",
            data : getData,
            success:function(data, textStatus, jqXHR)
            {
            //data: return data from server
            alert("Deleted the checked images");

            },
            error: function(jqXHR, textStatus, errorThrown)
            {
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

    <h3>Check the index</h3>
    <p>Click on one of the rows below to see all of the images since that search</p>
    <div>
    <table id="crawlerSearches" class="hoverTable">
        <tr>
            <th>Date</th>
            <th>Search parameter</th>
            <th>Number of images</th>
        </tr>
    </table>
    </div>


    <div>
    <form id="deleteImgs" action="/crawl" method="GET">

    </form>
    </div>

    <div class="modal"></div>



=======
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<html>
<head>
    <script type="text/javascript">
    function getSearches(){
        $.get('crawl', {get : 'crawlerSearches'}, function(responseJson) { //get the crawlerSearches using the crawler servlet
            $.each(responseJson, function(index, crawlerSearch) {  //for each CrawlerSearch object...
                $('<tr>').appendTo('#crawlerSearches')  //make a new row and add it to the table
                .append($('<td class="time">').text(crawlerSearch.time))  //add the time cell
                .append($('<td>').text(crawlerSearch.searchParam))  //add the searchParam cell
                .append($('<td>').text(crawlerSearch.numImg));  //add the numImg cell
            });
        });
    }
    getSearches();
    </script>

    <script>
    $(document).ready(function() {
        $('#crawlerSearches').on('click', 'tr', function() { //when you click a row in the table
            document.getElementById("deleteImgs").innerHTML = "Check the images that you want to delete.<br>";
            var time = $(this).find('td.time').html();
            $.get('crawl?get='+time, function(responseJson) {  //get the images since time using crawler servlet
                $.each(responseJson, function(index, item) {  //for each image since time...
                    var split = item.split(" ");
                    var $img = $('<img>').attr('src', split[0]);  //get the url
                    var $input = $('<input>');  //make a new input for the form
                    $input.attr('type', 'checkbox');
                    $input.attr('name', 'deleteMe');
                    $input.attr('value', item);
                    $input.appendTo('#deleteImgs');
                    $img.appendTo('#deleteImgs');
                    $('#deleteImgs').append('<br>');
                });
                var $submit = $('<input>');  //make the submit button
                $submit.attr('type', 'submit');
                $submit.attr('value', 'Delete checked images');
                $submit.appendTo('#deleteImgs');
            });
        });
    });
    </script>

    <script>
    $("#deleteImgs").submit(function(e) {
        var getData = $(this).serializeArray();
        var formURL = $(this).attr("action");
        $.ajax( {
            url : formURL,
            type: "GET",
            data : getData,
            success:function(data, textStatus, jqXHR)
            {
            //data: return data from server
            },
            error: function(jqXHR, textStatus, errorThrown)
            {
            //if fails
            }
        });
        e.preventDefault(); //STOP default action
        e.unbind(); //unbind. to stop multiple form submit.
        });
    </script>

    </head>
<body>

    <h3>Check the index</h3>
    <p>Click on one of the rows below to see all of the images since that search</p>
    <div>
    <table id="crawlerSearches" class="hoverTable">
        <tr>
            <th>Date</th>
            <th>Search parameter</th>
            <th>Number of images</th>
        </tr>
    </table>
    </div>


    <div>
    <form id="deleteImgs" action="/crawl" method="GET">

    </form>
    </div>




>>>>>>> 29a825f45c35bf427a77e2efa98edf1dab5d5ca9
</body>
</html>