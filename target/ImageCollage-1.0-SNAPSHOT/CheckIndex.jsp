<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<link href="../../stylesheets/CrawlerSearches.css" rel="stylesheet">

<t:template isApproved="${isApproved}" log="${log}">
    <jsp:attribute name="scripts">
    <script>
        $("#deleteImgs").submit(function(e) {
            var getData = $(this).serializeArray();
            var formURL = $(this).attr("action");
            $.ajax( {
                url : formURL,
                type: "POST",
                data : getData,
                success:function(data, textStatus, jqXHR)
                {
                    //data: return data from server
                    alert("Deleted the checked images");

                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    //if fails
                }
            });
            e.preventDefault(); //STOP default action
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
        getSearches();
        /*
         This function allows the user to click on one of the rows in the crawlerSearches table
         and view all of the images corresponding to that search
         */
        $(document).ready(function() {
            $('#crawlerSearches').on('click', 'tr', function() { //when you click a row in the table
                document.getElementById("deleteImgs").innerHTML = "<br><i>Check the images that you want to delete.</i><br>";
                var time = $(this).find('td.time').html();
                $.get('crawl?get='+time, function(responseJson) {  //get the images since time using crawler servlet
                    $.each(responseJson, function(index, item) {  //for each image since time...
                        var split = item.split(",");
                        var $img = $('<img>').attr('src', split[0]);  //get the url
                        var $input = $('<input>');  //make a new input for the form
                        $input.attr('type', 'checkbox');
                        $input.attr('name', 'deleteMe');
                        $input.attr('value', item);
                        $input.appendTo('#deleteImgs');
                        $img.appendTo('#deleteImgs');
                        $('#deleteImgs').append('<br>');
                    });
                    var $submit = $('<input>');  //make the submit button
                    $submit.attr('type', 'submit');
                    $submit.attr('value', 'Delete checked images');
                    $submit.appendTo('#deleteImgs');
                });
            });
        });
        </script>
    <script src="Loading.js">
    </script>

    </jsp:attribute>

    <jsp:attribute name="content">
    <p>Click on one of the rows below to see the images from that search</p>
    <div>
    <table id="crawlerSearches" class="hoverTable">
        <tr>
            <th>Date</th>
            <th>Search parameter</th>
            <th>Number of images</th>
        </tr>
    </table>
    </div>


    <div>
    <form id="deleteImgs" action="/crawl" method="GET">
    </form>
    </div>
    </jsp:attribute>

</t:template>