<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<style>
    body, .site-wrapper{
        background-image: none !important;
        background-color: white !important;
    }
    #divInfoBox{
        background-color: #eee;
        z-index : 1000;
        display: inline-block;
    }
    #mapId{
        z-index: 0;
    }

    #authAndTitle{
        display: inline-block;
        float: right;
    }
    .info{
        width: 100%;
        color: black !important;
    }
    #sidebar {
        background-color: #f5f5f5;
        border-left: 1px solid #eee;
        position: fixed;
        right: 0px;
        height: 100%;
        text-align: center;
    }
    table{
        border: none;
        border-top: 1px solid #2B2B40 !important;
        border-bottom: 1px solid #2B2B40 !important;
    }
    img[usemap], map area{
        outline: none;
    }
</style>

<t:template isApproved="${isApproved}" log="${log}" action="${action}">

    <jsp:attribute name="scripts">
        <script src="MakeMosaic.js"></script>


    </jsp:attribute>

    <jsp:attribute name="content">

            <div class="site-wrapper-inner">

                <div class="cover-container">
                    <div class="inner cover" class="inner cover">
                        <h1>Make a Mosaic</h1>
                        <form id="myForm" class="form-horizontal" action="${action}" method="post" role="form" enctype="multipart/form-data">

                            <div class="form-group">
                                <label for="userPic" class="col-sm-2 control-label">Your image</label>
                                <div class="col-sm-10">
                                    <input type="file" style="height:2em" id="userPic" name="userPic" accept="image/png, image/gif, image/jpeg, image/bmp, image/tiff, image/ico, image/webp" required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-sm-2 control-label">Smart-sizing</label>
                                <div class="col-sm-10">
                                    <input type="radio" name="smartSizing" checked="checked" value="true">Dynamically-sized blocks
                                    <span>       </span>
                                    <input type="radio" name="smartSizing" value="false">Statically-sized blocks
                                </div>
                            </div>



                            <div class="form-group">
                                <label for="depthIn" class="col-sm-2 control-label">Depth</label>
                                <div class="col-sm-10">
                                    <div><input id="depthIn" type="range" name="depth" min="2" max="7" value="5">
                                        <span id="showDepth"></span>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group" id = "threshSpan">
                                <label for="threshIn" class="col-sm-2 control-label" >Threshold</label>
                                <div class="col-sm-10">
                                    <input id="threshIn" type="range" name="threshold" min="500" max="1500" value="1000">
                                    <span id="showThresh"></span>
                                </div>
                            </div>

                            <div><input type="submit" class="btn btn-info" value="Make Mosaic"></div>
                        </form>
                    </div>
                </div>
            </div>

        <div id="mosaicContainer" class="container-fluid" style="display:none">
            <div class="row">
                <div class="col-sm-9 col-sm-10 col-sm-10" style="overflow-x:scroll; padding-top: 20px">
                    <div id="picDiv" class ="img-responsive">
                        <!--<img src="#" usemap="#actualmap" class="img-responsive" id="mosaic">-->
                        <map id="mapId" name="actualmap">
                        </map>
                    </div>

                    <div id ="Authors">
                        <a></a>
                    </div>
                </div>

                <div id="sidebar" class="col-sm-3 col-sm-2 col-sm-2 sidebar pull-right">
                    <div id="yourInputs">
                        <h5>Your inputs</h5>
                        <table>
                            <tr>
                                <p>Image</p>
                                <img src="#" id="yourImg">
                            </tr>
                        </table>
                    </div>
                    <div id="zoomedThumb" class="info img-responsive"></div>
                    <div class="info" style="color:black">Title: <a id="title"></a></div>
                    <div class="info" style="color:black">Author: <a id="author"></a></div>
                </div>
            </div>
        </div>

    </jsp:attribute>
</t:template>