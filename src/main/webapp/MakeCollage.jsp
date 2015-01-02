<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<t:template isApproved="${isApproved}" log="${log}" action="${action}">
    <jsp:attribute name="scripts">
        <script src="MakeCollage.js">
        </script>
    </jsp:attribute>

    <jsp:attribute name="content">

        <h3>Make a Mosaic</h3>
            
        <br>
            <form id="myForm" action=action method="post" enctype="multipart/form-data">
            <div>Choose the image for which you want to make a mosaic</div>
                <br>
            <div>
                <input type="file" name="userPic" accept="image/png, image/gif, image/jpeg, image/bmp, image/tiff, image/ico, image/webp" required>
            </div>
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


        <div id = "map">
            <map id = "mapId" name = "actualmap">

            </map>
        </div>

        <div id="divInfoBox">
        </div>


        <div id ="Authors">
            <a></a>
        </div>

    </jsp:attribute>
</t:template>