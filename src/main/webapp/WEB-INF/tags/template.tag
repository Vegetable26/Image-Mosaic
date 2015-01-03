
<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@ attribute name="content" fragment="true" %>
<%@ attribute name="scripts" fragment="true" %>
<%@ attribute name="wide" fragment="true" %>
<%@attribute name="isApproved" required="true"%>
<%@attribute name="log" required="true"%>
<%@attribute name="action" required="false"%>

<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Image Mosaic</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">


    <!-- Custom styles for this template -->
    <link href="../../stylesheets/cover.css" rel="stylesheet">
    <link href="../../stylesheets/LoadingGif.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>

    <![endif]-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

    <jsp:invoke fragment="scripts"/>

    <script>
        $(function () {
            var $body = $("body");
            $(document).ajaxStart(function () {
                $body.addClass("loading");
            });
            $(document).ajaxStop(function () {
                $body.removeClass("loading");
            });
            var height = $('#navbar').height();
            $body.css("padding-top", height+"px");
        });
    </script>


</head>

<body>

<div class="site-wrapper">


    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">Image Collage</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="/getting_started">Getting Started</a></li>
                    <li><a href="/make_mosaic">Make a Mosaic</a></li>
                    <% if (isApproved.compareTo("1") == 0){
                        out.println("<li><a href=\"populate_index\">Populate Index</a></li>");
                        out.println("<li><a href=\"check_index\">Check Index</a></li>");
                    }
                    %>
                    <li><a href="contact">Contact</a></li>
                    <%
                        if (isApproved.compareTo("1") == 0){
                            out.println("<li><a href=\"" + log +  "\">Log Out</a></li>");
                        }
                        else{
                            out.println("<li><a href=\"" + log +  "\">Approved User?<br>Log In</a></li>");
                        }
                    %>
                </ul>
            </div><!--/.nav-collapse -->
        </div>
    </nav>
    <jsp:invoke fragment="content"/>




            </div>

<div class="modal"></div>

<footer class="footer">
    <div class="container">
        <p style="line-height: 60px">&copy; 2014-2015</a>, Joseph Hwang and Nicholas Kwon.</p>
    </div>
</footer>
<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<!--<script src="../../assets/js/docs.min.js"></script>-->
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<!--<script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>-->
</body>
</html>