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

    <!--
    <script>
    function showForm(whichForm){
    var form = "hi";
    if (whichForm == 1){
        form = document.getElementById("collage_form").innerHTML;
    }
    if (whichForm == 2){
        form = document.getElementById("index_form").innerHTML;
    }
    else{
        form = document.getElementById("gallery_form").innerHTML;
    }
    document.getElementById("content").innerHTML = form;
    }
    </script>
    -->
    <script>
    function showForm(whichForm)
    {
    var xmlhttp;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
    xmlhttp=new XMLHttpRequest();
    }
    else
    {// code for IE6, IE5
    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange=function()
    {
    if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
    document.getElementById("form").innerHTML=xmlhttp.responseText;
    }
    }
    var openMe;
    if (whichForm == 1){
        openMe = "MakeCollage.jsp";
    }
    if (whichForm == 2){
        openMe = "PopulateIndex.jsp";
    }
    if (whichForm == 3){
        openMe = "CheckIndex.jsp";
    }
    xmlhttp.open("GET",openMe,true);
    xmlhttp.send();
    }
    </script>


</head>

<body>
    <h1><a href="/">Image Collage </a></h1>
    <div id="content">
    <h3>Do you want to: </h3>
    <button type="button" onclick="showForm(1)">Make a collage</button>
    <button type="button" onclick="showForm(2)">Populate the index</button>
    <button type="button" onclick="showForm(3)">Check the index</button>
    </div>

    <div id="form"></div>

</body>
</html>