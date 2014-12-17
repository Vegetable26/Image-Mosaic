<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

        <html>


        <head>
            <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
            <script type="text/javascript"
                src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
            <script>
                $(document).ready(function() {                           // When the HTML DOM is ready loading, then execute the following function...
                    $('div.crawlerSearches table tr').live('click', function() {                  // Locate HTML DOM element with ID "somebutton" and assign the following function to its "click" event...
                        $.get('crawl?get='+$('this').find('#time').html(), function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
                            var $ul = $('<ul>').appendTo($('#gallery')); // Create HTML <ul> element and append it to HTML DOM element with ID "somediv".
                                $.each(responseJson, function(index, item) { // Iterate over the JSON array.
                                    $('<li>').text(item).appendTo($ul);      // Create HTML <li> element, set its text content with currently iterated item and append it to the <ul>.
                                });
                            });
                        });
                    });
            </script>

            <script type="text/javascript">
            $(document).ready(function() {   // When the HTML DOM is ready loading, then execute the following function...
                $.get('crawl', {get : 'crawlerSearches'}, function(responseJson) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
                    var $table = $('<table>').appendTo($('#content')); // Create HTML <ul> element and append it to HTML DOM element with ID "somediv".
                        $.each(responseJson, function(index, crawlerSearch) { // Iterate over the JSON array.
                            $('<tr>').appendTo($table)
                                .append($('<td id ="time">').text(crawlerSearch.time))
                                .append($('<td>').text(crawlerSearch.searchParam))
                                .append($('<td>').text(crawlerSearch.numImg));
                            });
                        });
                    });
            </script>
            <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
        </head>

        <body>

        <h3>Check the index</h3>
        <h5>previous searches</h5>

        <div id="crawlerSearches">Hwllo</div>

        <form action="/crawl" method="get">
        <div>Look at pictures since:</div>
        <div><input type="text" name="date"></div>
        <div><button id="getPictures" type="button"></div>
        </form>

        <div id="gallery"></div>

        </body>
        </html>