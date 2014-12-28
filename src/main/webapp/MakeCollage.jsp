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

    <script>
        /*
        This script submits the form the build a collage with ajax.
        Upon success, it will display the collage and layer it's
        attribution map on top.
         */
        $( '#myForm' ).submit( function( e ) {
            //get the action url
            var formURL = $(this).attr("action");
            $.ajax( {
                url: formURL,
                type: 'POST',
                data: new FormData( this ),
                dataType: 'json',
                processData: false,
                contentType: false,
                //upon successful completion of the form
                success: function(resp){
                    //make an img and set its src, height, and width from resp
                    $('<img>').attr({
                        src: resp.url,
                        height: resp.height,
                        width:resp.width,
                        usemap: '#actualmap',
                        id: 'collage',
                        name:'collage'
                    }).appendTo('#picDiv');
                    //iterate over the attriution table
                    for(var i = 0; i<resp.attributionTable.length; i++){
                        var attribute = resp.attributionTable[i];
                        //make an area, set its attributes, make it of class thumbnail, and add it to the mapId
                        var area = $('<area>');
                        area.attr({
                            //the id will have the necessary info for #divInfoBox
                            id: attribute.url+","+attribute.author+","+attribute.title+","+attribute.id+","+attribute.trueUrl,
                            shape:'rect',
                            coords: attribute.x1 +',' + attribute.y1 + ',' + attribute.x2 +',' + attribute.y2 ,
                            href:attribute.trueUrl,
                            target:"_blank",
                            'class': "thumbnail"
                        }).appendTo('#mapId');

                    }
                    //make a link to download the image
                    var link = $('<a>',{
                        text: "Download image",
                        download: "Collage.png",
                        href:resp.url,
                        click: function(){alert('Downloading image');}
                    }).addClass('button');
                    link.after('<button type="button">Click Me!</button>');
                    link.appendTo('#Authors');
                }
            });
            e.preventDefault();

        } );
    </script>

    <script>
        /* This function will show the #divInfoBox when an area
         is hovered over. It will hide the #divInfoBox if the mouse
         isn't hovering over the area anymore.
         */

        function toggleInfoBox() {
            var thisTimer = null;
            var timeoutTime = 100;
            var insidePopup = false;
            var ibox = $("#divInfoBox");
            //set the flag for whether we're in the pop up
            ibox.mouseenter(function(){
                insidePopup = true;
            }).mouseleave(function(){
                insidePopup = false;
                //if we're not in the popup, hide it
                ibox.hide();
            });

            $('#mapId').on("mouseover", "area", function(){
                //the info from the id is comma-delimited
                var info = this.id.split(",");
                showInfoBox(info[0], info[1], info[2], info[3], info[4], this);
                clearTimeout(thisTimer);
                //if we leave the area
                $(this).on("mouseleave", function(){
                    //check if we're on another area
                    $('#mapId').on("mouseover", "area", function() {
                        //if we are, do the same thing as before
                        var info = this.id.split(",");
                        showInfoBox(info[0], info[1], info[2], info[3], info[4], this);
                        clearTimeout(thisTimer);
                    });
                    //now we need to hide the old popup
                    clearTimeout(thisTimer);
                    thisTimer = setTimeout(function(){
                        if(!insidePopup) {
                            ibox.hide();
                        }
                        clearTimeout(thisTimer);
                    }, timeoutTime);
                });
            });
        }

    </script>

    <script>
        $(function() {
            toggleInfoBox();
        });
    </script>

    <script>
        /*
        This function will add the img, author, and title
        to the #divInfoBox and display it.
         */
        showInfoBox = function(url, author, title, id, trueUrl, area) {
            var areaElement = $(area);
            var offset = areaElement.position();
            var ibox = $("#divInfoBox");
            ibox.offset({top: 0, left: 0});
            ibox.html("");
            ibox.hide();
            var img = $('<img>');
            var imgLink = $('<a>').attr("href", trueUrl);
            ibox.append(imgLink.append(img.attr("src", url).css("float", "left")));
            //img.attr("src", url).css("float", "left").appendTo(imgLink.appendTo(ibox));
            var div = $('<div>').attr("id", "authAndTitle");
            var userLink = $('<a>').attr("href", "https://www.flickr.com/photos/" +  id);
            div.append(userLink.append($('<h5>').html(author)));
            div.append($('<h6>').html(title));
            div.appendTo(ibox);
            var coords = area.coords.split(",");
            console.log(ibox.height());
            moveLeft = areaElement.outerWidth() + parseInt(coords[2]) + offset.left;
            moveDown = offset.top + parseInt(coords[3]) - parseInt($('#collage').attr("height"));
            ibox.show();
            ibox.offset({top: moveDown - ibox.height(), left: moveLeft});

        };
    </script>


    <script>
        /* this script shows the login link if there isn't already a logout link */
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
        <h5>
            This web app makes a mosaic of flickr thumbnails from one of your photos.
        </h5>
    <br>
        <form id="myForm" action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
        <div>Choose the image for which you want to make a collage</div>
            <br>
        <div><input type="file" name="userPic"></div>
            <br>
        <div>Threshold: when should we divide a block? Larger values will result in dividng less often.</div>
            <br>
        <div><input id="threshIn" type="range" name="threshold" min="500" max="1500" value="1000">
        <span id="showThresh"></span>
        </div>

        <script>
            $('#showThresh').html($('#threshIn').val());
            $('#threshIn').change( function() {
                $('#showThresh').html($('#threshIn').val());
            });
        </script>
        <br>
        <div>Depth: how small do you want the blocks to get? Larger values will result in smaller blocks.</div>
            <br>
        <div><input id="depthIn" type="range" name="depth" min="2" max="7" value="5">
        <span id="showDepth"></span>
        </div>

        <script>
            $('#showDepth').html($('#depthIn').val());
            $('#depthIn').change( function() {
                $('#showDepth').html($('#depthIn').val());
            });
        </script>

        <div><input type="checkbox" name="smartSizing">Dynamically-sized blocks</div>
        <br>
        <div>Scaling factor: how many times larger should the collage be than the inputted image? Larger values will result in a higher definition collage.</div>
            <br>
        <div><input id="scaleIn" type="range" name="inputFactor" min="1" max="5" value="2">
        <span id="showScale"></span>
        </div>

        <script>
            $('#showScale').html($('#scaleIn').val());
            $('#scaleIn').change( function() {
                $('#showScale').html($('#scaleIn').val());
            });
        </script>

            <br>
        <div><input type="submit" value="make the collage"></div>
        </form>

    <div id="picDiv">

    </div>

    <!--<div class="modal"></div>-->

    <div class="logInOut" id="logIn" style="visibility:hidden">
    <a href="<%= userService.createLoginURL("/") %>">Log in</a>
    </div>

    <div id = "map">
        <map id = "mapId" name = "actualmap">

        </map>
    </div>

    <div id="divInfoBox">
    </div>


    <div id ="Authors">
        <a></a>
    </div>
    </body>

    </html>
