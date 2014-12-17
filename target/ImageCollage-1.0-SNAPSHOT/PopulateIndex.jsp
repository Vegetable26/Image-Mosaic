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
        <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

        <script>
        $("#theForm").submit(function(e)
        {
        var getData = $(this).serializeArray();
        var formURL = $(this).attr("action");
        $.ajax(
        {
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

        $("#theForm").submit();</script>
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


        </body>

        </html>