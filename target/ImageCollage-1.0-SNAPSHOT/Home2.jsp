<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>


<%
    /*
    This script can:
        1) check if the current user is an approvedUser
     */
    //initialize the services that we need
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    UserService userService = UserServiceFactory.getUserService();

    //our approved users
    String[] approvedUsers = {"nichkwon@gmail.com", "jhwang261@gmail.com"};

    //1 if the current user is approved; 0 otherwise
    int isApproved = 0;
    //get the current user
    User user = userService.getCurrentUser();
    if (user != null) {
        for (String approvedUser : approvedUsers) {
            //check if the email of the currentUser matches that of one of the approvedUsers
            if (approvedUser.compareTo(user.getEmail()) == 0) {
                isApproved = 1;
            }
        }
    }
%>


<html>

<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
    <link type="text/css" rel="stylesheet" href="/stylesheets/LoadingGif.css"/>
    <link type="text/css" rel="stylesheet" href="/stylesheets/CrawlerSearches.css"/>

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

    <script src="Home.js">
    </script>


    <script>
        //call the function with argument isApproved
        showPage(<%=isApproved%>);
    </script>



</head>

<body>

<h1><a href="/">Image Collage</a></h1>

<div id="content"></div>

<div class="modal"></div>


</body>
</html>