<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
UserService userService = UserServiceFactory.getUserService();

%>

        <html>

        <head>
        <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
        <link type="text/css" rel="stylesheet" href="/stylesheets/LoadingGif.css"/>
        <link type="text/css" rel="stylesheet" href="/stylesheets/CrawlerSearches.css"/>

        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

        <script>
        $( '#myForm' ).submit( function( e ) {
        var formURL = $(this).attr("action");
        $.ajax( {
        url: formURL,
        type: 'POST',
        data: new FormData( this ),
        processData: false,
        contentType: false,

        success: function(resp){
        $('<img>').attr('src', resp).appendTo('#picDiv');
            <% boolean submitted = true; %>
        }

        });
        e.preventDefault();
        //e.unbind(); //unbind. to stop multiple form submit.

        } );
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
        <h1><a href="/">Image Collage </a></h1>

        <h3>Make a collage</h3>
        <form id="myForm" action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
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

        <div id="picDiv">
            <%/*
    if (submitted){
    String url = (String)request.getParameter("url");
    out.print(url);

    while (url == null){
        url = (String)request.getParameter("url");
    }
    out.print("<img src=\""+url+"\">");
    }*/
    %>
        </div>

        <div id="signIn">
        <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
        </div>

        <div class="modal"></div>

        </body>

        </html>