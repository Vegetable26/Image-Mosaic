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
</head>

<body>

    <h3>Make a collage</h3>
        <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
        <div>Choose the image you want to collage-ify</div>
        <div><input type="file" name="userPic"></div>
        <div>Choose a theme for the collage</div>
        <div><input type="text" name="theme"></div>
        <div>enter the threshold</div>
        <div><input type="text" name="threshold"></div>
        <div>enter the max depth</div>
        <div><input type="text" name="depth"></div>
        <div>enter the scaling factor</div>
        <div><input type="text" name="inputFactor"></div>
        <div><input type="submit" value="make da collage"></div>
        </form>


    </body>

    </html>
