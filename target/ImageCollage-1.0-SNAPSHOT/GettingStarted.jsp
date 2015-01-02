<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:template isApproved="${isApproved}" log="${log}">
    <jsp:attribute name="content">
        <div class = "container-fluid" style="background-color:rgba(50,50,50,0.5); color:white ">
            <div class="row">
                <div class = center class="inner cover" style="text-align: left">
                    <h1 class = center> Getting Started </h1>
                    <h3 style="text-align: left; padding-left: 30"> Introduction to Smartsizing</h3>
                    <p style="padding:5 50"> Smartsizing lies at the core of the Image-Mosaic algorithm and can produce a
                        more visually-diverse mosaic. The algorithm works by dynamically sizing the mosaic-images to capture
                        important features within the base image. Important features are represented by a
                        larger number of smaller sized mosaic images, while background features are represented by much larger
                        sized mosaic images. Below are two examples of a smartsized and non-smartsized mosaic.
                    </p>
                </div>

            </div>

            <div class="row">
                <div class="col-md-4 col-md-push-4">
                    <h4>Original Image</h4>
                    <a href="https://www.flickr.com/photos/deniwlp84/14451399403/" target="_blank">
                        <img 	src= "images/Parrot.jpg" class = "img-responsive img-					thumbnail">
                    </a>

                </div>
                <div class="col-md-4 col-md-pull-4 col-sm-4">
                    <h4>Smart-Sized Mosaic</h4>
                    <img 	src= "images/ParrotSmart.png" class = "img-responsive img-					thumbnail">

                </div>
                <div class="col-md-4 col-sm-4">
                    <h4>Normal Mosaic</h4>
                    <img 	src= "images/ParrotDumb.png" class = "img-responsive img-thumbnail">

                </div>


            </div>
            <p class="center">Blue-and-gold Macaw by Deni Williams: https://www.flickr.com/photos/deniwlp84/14451399403/</p>

            <div class="row">
                <div class = center class="inner cover" style="text-align: left">
                    <h3 style="text-align: left; padding-left: 30"> Depth</h3>
                    <p style="padding:5 50"> Depth determines the minimum size of a mosaic-image within the mosaic. The higher the value the lower the minimum
                        size becomes. For the highest resolution mosaics, select a depth of 7 or 8. As of now, the max depth for
                        smart-sized mosaics is 8 and the max depth for non-smart-sized images is 7. Depth 8 non-smart-sized mosaics will be
                        released soon.
                    </p>
                </div>

            </div>


            <div class="row">
                <div class="col-md-4 col-md-push-4">
                    <h4>Original Image </h4>
                    <a href="https://www.flickr.com/photos/deniwlp84/14427944351/" target="_blank">
                        <img src="images/Bird.jpg" class = "img-responsive img-					thumbnail">
                    </a>

                </div>
                <div class="col-md-4 col-md-pull-4 col-sm-4">
                    <h4>Depth 6</h4>
                    <img 	src= "images/LowResBird.png" class = "img-responsive img-					thumbnail">

                </div>
                <div class="col-md-4 col-sm-4">
                    <h4>Depth 8 </h4>
                    <img 	src= "images/HighResBird.png" class = "img-responsive img-thumbnail">
                </div>


            </div>
            <p class="center">Bird's Park by Deni Williams: https://www.flickr.com/photos/deniwlp84/14427944351/</p>

            <div class="row">
                <div class = center class="inner cover" style="text-align: left">
                    <h3 style="text-align: left; padding-left: 30"> Threshold</h3>
                    <p style="padding:5 50"> The threshold determines the dynamic sizing of the mosaic-images during smart sizing. A lower
                        threshold will tend to create mosaics with smaller images. Thus, the lower the threshold the sharper the image
                        will be. Notice how the edges in the 500 Threshold mosaic are more distinct than those of the 1500 Threshold mosaic.
                    </p>
                </div>

            </div>

            <div class="row">
                <div class="col-md-4 col-md-push-4">
                    <h4>Original Image </h4>
                    <a href="https://www.flickr.com/photos/deepblue66/12384033395/" target="_blank">
                        <img src="images/Drums.jpg" class = "img-responsive img-thumbnail">
                    </a>

                </div>
                <div class="col-md-4 col-md-pull-4 col-sm-4">
                    <h4>Threshold: 500</h4>
                    <img 	src= "images/LowThreshDrums.png" class = "img-responsive img-					thumbnail">

                </div>
                <div class="col-md-4 col-sm-4">
                    <h4>Threshold: 1500</h4>
                    <img 	src= "images/HighThresh.png" class = "img-responsive img-thumbnail">
                </div>
            </div>
            <p class="center">Ladakh Festival 2013 by Dietmar Temps: https://www.flickr.com/photos/deepblue66/12384033395/</p>
        </div>

        <style>
            .center{
                text-align:center;
            }
        </style>
    </jsp:attribute>
</t:template>