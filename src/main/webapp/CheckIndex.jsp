<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<link href="../../stylesheets/CrawlerSearches.css" rel="stylesheet">

<style>
    body, .site-wrapper{
        background-image: none !important;
        background-color: white !important;
    }
    @media (min-width: 768px){
        .site-wrapper-inner {
            vertical-align: top !important;
        }
    }
    .overlay{
        position:   absolute;
        z-index:    1000;
        top:        0;
        /*left:       0;*/
        height:     100%;
        /*width:      100%;*/
        background: rgba( 255, 255, 255, .8 );
    }

</style>
<t:template isApproved="${isApproved}" log="${log}">
    <jsp:attribute name="scripts">
    <script>
        $(function() {
            $("#deleteImgs").submit(function (e) {
                var getData = $(this).serializeArray();
                var formURL = $(this).attr("action");
                $.ajax({
                    url: formURL,
                    type: "POST",
                    data: getData,
                    success: function (data, textStatus, jqXHR) {
                        //data: return data from server
                        $('')
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        //if fails
                    }
                });
                e.preventDefault(); //STOP default action
            });
        });
        /*
         This function builds the table of crawlerSearches by making a GET request and reading
         the returned JSON
         */
        function getSearches(){
            $.get('crawl', {get : 'crawlerSearches'}, function(responseJson) { //get the crawlerSearches using the crawler servlet
                $.each(responseJson, function(index, crawlerSearch) {  //for each CrawlerSearch object...
                    $('<tr>').appendTo('#crawlerSearches')  //make a new row and add it to the table
                            .append($('<td class="time">').text(crawlerSearch.time))  //add the time cell
                            .append($('<td>').text(crawlerSearch.searchParam))  //add the searchParam cell
                            .append($('<td>').text(crawlerSearch.numImg));  //add the numImg cell
                });
            });
        }
        $(function () {
            getSearches();
        });
        /*
         This function allows the user to click on one of the rows in the crawlerSearches table
         and view all of the images corresponding to that search
         */
        $(document).ready(function() {
            $('#crawlerSearches').on('click', 'tr', function () { //when you click a row in the table
                var time = $(this).find('td.time').html();
                $.ajax({
                    url: 'crawl?get=' + time,
                    type: "GET",
                    success: function (responseJson) {
                        $('#picRow').html("");
                        $('#deleteImgs').prepend("<br><i>Check the images that you want to delete.</i><br>");
                        //get the images since time using crawler servlet
                        $.each(responseJson, function (index, item) {  //for each image since time...
                            var split = item.split(",");
                            var $img = $('<img>').attr('src', split[0]);  //get the url
                            var $input = $('<input>');  //make a new input for the form
                            $input.attr('type', 'checkbox');
                            $input.attr('name', 'deleteMe');
                            $input.attr('value', item);
                            var col = $('<div>').addClass("col-sm-12 col-md-4 col-lg-2");
                            col.css("padding", "5px");
                            //col.css("text-align", "center");
                            $img.css("padding", "5px");
                            $img.addClass("img-thumbnail");
                            $img.css("height", "130px");
                            $img.css("width", "auto");
                            $input.appendTo(col);
                            $img.appendTo(col);
                            col.appendTo('#picRow');

                            //$('#deleteImgs').append('<br>');
                        });
                        $('input').hide();
                        var $submit = $('<input>');  //make the submit button
                        $submit.attr('type', 'submit');
                        $submit.attr('value', 'Delete checked images');
                        $submit.appendTo('#deleteImgs');
                    }
                });
            });
        });

        $(function() {
            $('#picRow').on("click", "div", function(){
                var checkbox = $(this).find('input')[0];
                console.log(checkbox);
                console.log($(checkbox).attr("type"));
                var checked = $(checkbox).is(':checked');
                if (checked == true){
                    $(checkbox).prop("checked", false);
                    $($(this).find('.overlay')[0]).remove();
                }
                else{
                    $(checkbox).prop("checked", true);
                    var overlay = $('<div>').addClass("overlay");
                    var width = $($(this).find('img')[0]).width();
                    console.log(width);
                    overlay.width(width+10);
                    overlay.css("left", ($(this).width()-width)/2);
                    overlay.append('<span class="glyphicon glyphicon-remove-circle" style="line-height: 140px">');
                    $(this).append(overlay);
                }
            });
        });
        </script>

    </jsp:attribute>


    <jsp:attribute name="content">
        <div class="container-fluid" style="padding-left: 20px; padding-right: 20px">
            <div class="crawlerSearches" style="padding-top: 10px">
                <p>Click on one of the rows below to see the images from that search</p>
                <table id="crawlerSearches" class="table table-hover">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Search parameter</th>
                            <th>Number of images</th>
                        </tr>
                    </thead>

                </table>
            </div>

            <form id="deleteImgs" action="/crawl" method="GET">
                    <div class="row" id="picRow">
                    </div>
            </form>
        </div>
    </jsp:attribute>



</t:template>