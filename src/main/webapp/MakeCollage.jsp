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

    <script src="ImageMapster.js" type="text/javascript">
        $( '#myForm' ).submit( function( e ) {
            var formURL = $(this).attr("action");
            $.ajax( {
                url: formURL,
                type: 'POST',
                data: new FormData( this ),
                dataType: 'json',
                processData: false,
                contentType: false,

                success: function(resp){

                    $('<img>').attr({
                        src: resp.url,
                        height: resp.height,
                        width:resp.width,
                        usemap: '#actualmap',
                        id: 'collage',
                        name:'collage'
                        }).appendTo('#picDiv');

                    for(i = 0; i<resp.attributionTable.length;i++){
                        var attribute = resp.attributionTable[i];
                        $('<area>').attr({
                        shape:'rect',
                        coords: attribute.x1 +',' + attribute.y1 + ',' + attribute.x2 +',' + attribute.y2 ,
                        href:attribute.trueUrl,
                        target:"_blank"
                        }).appendTo('#mapId');
                    }
                    var link = $('<a>',{
                        text: "Download image",
                        download: "Collage.png",
                        href:resp.url,
                        click: function(){alert('Downloading image');}
                    });
                    link.after('<button type="button">Click Me!</button>');
                    link.appendTo('#Authors');
                }
            });
            e.preventDefault();
            //e.unbind(); //unbind. to stop multiple form submit.

        } );
    </script>

    <script>
        alert("starting script");
        if (document.getElementById("logOut") == null){
            document.getElementById("logIn").style.visibility = 'visible';
        }
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

    </div>

    <div class="modal"></div>

    <div class="logInOut" id="logIn" style="visibility:hidden">
    <a href="<%= userService.createLoginURL("/") %>">Log in</a>
    </div>

    <div id = "map">
        <map id = "mapId" name = "actualmap">

        </map>
    </div>



    <div id ="Authors">
        <a></a>
    </div>
    </body>

    </html>
