<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>


<html>
<%     UserService userService = UserServiceFactory.getUserService(); %>
<head>

    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
    <link type="text/css" rel="stylesheet" href="/stylesheets/LoadingGif.css"/>
    <link type="text/css" rel="stylesheet" href="/stylesheets/CrawlerSearches.css"/>

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

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

    <h3>Do you want to: </h3>
    <button type="button" onclick="showForm(1)">Make a collage</button>
    <button type="button" onclick="showForm(2)">Populate the index</button>
    <button type="button" onclick="showForm(3)">Check the index</button>

    <div id="form"></div>


    <div class="logInOut" id="logOut">
    <a href="<%= userService.createLogoutURL("/") %>">Log out</a>
    </div>
    </body>
</html>
