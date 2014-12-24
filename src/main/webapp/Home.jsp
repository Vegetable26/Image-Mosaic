<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.datastore.*" %>

<%
DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
Entity me = new Entity("approvedUser", "nichkwon@gmail.com");
Entity joe = new Entity("approvedUser", "jhwang261@gmail.com");
//datastore.put(me);
//datastore.put(joe);
BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
UserService userService = UserServiceFactory.getUserService();
int isApproved = 0;
User user = userService.getCurrentUser();

if (user != null) {
    Query q = new Query("approvedUser");
    //System.out.println("the user is " + user.getEmail());
    PreparedQuery pq = datastore.prepare(q);
    for (Entity approvedUser : pq.asIterable()) {
        if (approvedUser.getKey().getName().compareTo(user.getEmail()) == 0) {
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

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

    <script>
    //this function will display a page below the current page based on the input parameter isApproved
    function showPage(isApproved){
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
                alert("almost");
                //set the content div to have the html content defined by xmlhttp.open
                var form = document.getElementById("content");
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
        if (isApproved == 0){
            openMe = "MakeCollage.jsp";
        }
        else{
            openMe = "DoEverything.jsp";
        }
        xmlhttp.open("GET",openMe,true);
        xmlhttp.send();
    }
    showPage(<%=isApproved%>);
    </script>

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
    <h1><a href="/">Image Collage</a></h1>

    <div id="content"></div>

    </body>
</html>