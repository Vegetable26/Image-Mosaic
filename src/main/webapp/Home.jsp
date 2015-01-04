<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<style>
    .site-wrapper{
        background-color: rgba(50,50,50,0.5);
        color: white;
    }
</style>

<t:template isApproved="${isApproved}" log="${log}">
    <jsp:attribute name="content">
        <div class="site-wrapper-inner">

            <div class="cover-container">
                <div id="content" class="inner cover">
                <h1 class="cover-heading">Make a mosaic of flickr thumbnails.</h1>
                <p class="lead">This web app will make a mosaic of flickr thumbnails from a photo that you upload.</p>
                <p class="lead">
                    <a href="getting_started" class="btn btn-lg btn-default">Get Started</a>
                </p>
                </div>
            </div>
        </div>


    </jsp:attribute>
</t:template>
