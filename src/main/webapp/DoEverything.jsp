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
    //this function will display a form below the current page based on the input parameter whichForm
    function showForm(whichForm){
        var xmlhttp;
        // code for IE7+, Firefox, Chrome, Opera, Safari
        if (window.XMLHttpRequest){
            xmlhttp=new XMLHttpRequest();
        }
        // code for IE6, IE5
        else{
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange=function(){
            if (xmlhttp.readyState==4 && xmlhttp.status==200){
                //set the form div to have the html content defined by xmlhttp.open
                var form = document.getElementById("form");
                form.innerHTML=xmlhttp.responseText;
                var myScripts = form.getElementsByTagName("script");
                if (myScripts.length > 0) {
                    for (i = 0; i < myScripts.length; i++){
                    eval(myScripts[i].innerHTML);
                    }
                }
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
    <h3>Do you want to: </h3>
    <button type="button" onclick="showForm(1)">Make a collage</button>
    <button type="button" onclick="showForm(2)">Populate the index</button>
    <button type="button" onclick="showForm(3)">Check the index</button>

    <div id="form"></div>

    <div class="modal"></div>

    <div class="logInOut" id="logOut">
    <a href="<%= userService.createLogoutURL("/") %>">Log out</a>
    </div>

    </body>
</html>