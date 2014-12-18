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
    //this function will display a form below the current page based on the input parameter whichForm
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
    //set the form div to have the html content defined by xmlhttp.open
<<<<<<< HEAD
    document.getElementById("form").innerHTML=xmlhttp.responseText;
=======
    var form = document.getElementById("form");
    form.innerHTML=xmlhttp.responseText;
    var myScripts = form.getElementsByTagName("script");
    if (myScripts.length > 0) {
    for (i = 0; i < myScripts.length; i++){
        eval(myScripts[i].innerHTML);
    }
    }
>>>>>>> 29a825f45c35bf427a77e2efa98edf1dab5d5ca9
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