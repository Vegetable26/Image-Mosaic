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
    <script src="/Users/compsci/Downloads/AbsoluteBeginners/scripts/jquery.imagemapster.js" type="text/javascript">
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

                    alert( "Trying to parse json");
                    alert(JSON.stringify(resp));
                    $('<img>').attr({
                        src: resp.url,
                        height: 700,
                        width:700,
                        usemap: '#actualmap',
                        id: 'collage',
                        name:'collage'
                        }).appendTo('#picDiv');
                    //$('<img>').attr('src', resp.url,'usemap',"#actualmap",'width','1000','height','1000').appendTo('#picDiv');


                    alert("done appending dormer");


                    alert("about to star thte rest");
                    for(i = 0; i<resp.attributionTable.length;i++){
                        var attribute = resp.attributionTable[i];
                        //alert(resp.attributionTable[i].author)
                        $('<area>').attr({
                        shape:'rect',
                        coords: attribute.x1 +',' + attribute.y1 + ',' + attribute.x2 +',' + attribute.y2 ,
                        //coords: num.toString(attribute.x1) +',' + num.toString(attribute.y1) + ',' + num.toString(attribute.x2) +',' + num.toString(attribute.y2) ,
                        //href:attribute.url
                        href:attribute.url
                        }).appendTo('#mapId');
                        $('<p>'+ resp.attributionTable[i].author +'</p>').appendTo('#loser');
                    }
                    alert("trying to mapster");

                    $('#imgMe').mapster({
                        mapKey: 'name',
                        singleSelect: true,
                        altImage: 'examples/images/frog_map_alt.jpg',
                        altImageOpacity: 0.8,
                        fillOpacity: 0.5,
                        fillColor: 'f4ff75'
                    });
                    alert("mapstered");

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

<div id="picDiv"></div>

<div class="modal"></div>


<div id = "map">
    <map id = "mapId" name = "actualmap">
        <area shape="rect" coords="0,0,1,1" href="http://cdn.images.express.co.uk/img/dynamic/79/590x/dormer1-475628.jpg">
    </map>
</div>



<div id ="loser">
    <img id = "imgMe" src = "http://cdn.images.express.co.uk/img/dynamic/79/590x/dormer1-475628.jpg">
    <p> Hi faggot </p>
</div>


</body>
