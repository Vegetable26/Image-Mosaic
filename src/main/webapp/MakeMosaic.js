/**
 * Created by nich on 1/2/15.
 */
/*
 This script submits the form the build a collage with ajax.
 Upon success, it will display the collage and layer it's
 attribution map on top.
 */
var depth, smartSizing, thresh, image;
$(function () {
    $( '#myForm' ).submit( function( e ) {
        //get the action url
        var formURL = $(this).attr("action");
        var userPic = document.getElementById('userPic');
        depth = document.getElementById('depthIn').value;
        smartSizing = $('input[name="smartSizing"]:checked').val().localeCompare("true") == 0;
        thresh = null;
        if (smartSizing) {
            thresh = document.getElementById('threshIn').value;
        }
        $.ajax( {
            url: formURL,
            type: 'POST',
            data: new FormData( this ),
            dataType: 'json',
            processData: false,
            contentType: false,
            //upon successful completion of the form
            success: function(resp) {
                $('.site-wrapper-inner').hide();
                $('#mosaicContainer').show();
                //make an img and set its src, height, and width from resp

                var viewWidth = Math.max(document.documentElement.clientWidth, window.innerWidth || 0)*0.85;
                //console.log("size is"+viewWidth);
                var viewHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
                var scale = Math.min(viewWidth/resp.width,viewHeight/resp.height);
                console.log("scale is"+scale);

                $('<img>').attr({
                    id: "mosaic",
                    src: resp.url,
                    usemap: '#actualmap',
                    id: 'mosaic',
                    width: (resp.width*scale).toString(),
                    height: (resp.height*scale).toString()



                }).prependTo('#picDiv');

                // var xScale = document.getElementById("mosaic").getAttribute("width")/resp.width;

                //console.log("calculated xScale"+xScale);
                //console.log("oldHeight is"+document.getElementById("mosaic").getAttribute("height"));
                //console.log("height is"+resp.height);

                // var yScale =  xScale;

                //console.log("calculated yScale"+ yScale);
                //var yScale = document.getElementById("mosaic").getAttribute("height")/parseInt(resp.height);
                //alert("New scales are"+xScale+", "+yScale);
                //iterate over the attriution table


                for (var i = 0; i < resp.attributionTable.length; i++) {
                    var attribute = resp.attributionTable[i];
                    //make an area, set its attributes, make it of class thumbnail, and add it to the mapId
                    var area = $('<area>');
                    var x1 = Math.round(parseInt(attribute.x1)*scale);
                    var x2 = Math.round(parseInt(attribute.x2)*scale);
                    var y1 = Math.round(parseInt(attribute.y1)*scale);
                    var y2 = Math.round(parseInt(attribute.y2)*scale);

                    //var x2 = parseFloat(attribute.x2)*xScale;
                    area.attr({
                        //the id will have the necessary info for #divInfoBox
                        id: attribute.url + "," + attribute.author + "," + attribute.title + "," + attribute.id + "," + attribute.trueUrl,
                        shape: 'rect',
                        coords: x1.toString() + ',' + y1.toString() + ',' + x2.toString() + ',' + y2.toString(),
                        href: "#"
                        //target:"_blank"
                    }).appendTo('#mapId');
                    //console.log(x1+","+y1+";"+x2+","+y2);

                }
                //make a link to download the image
                var link = $('<a>', {
                        text: "Download Mosaic",
                        download: "Collage.png",
                        href: resp.url
                    }
                ).addClass('btn btn-info').css("position", "absolute").css("bottom", "100px").css("left", "7px");
                //link.after('<button type="button">Click Me!</button>');
                link.appendTo('#sidebar');
                var glyph = "<i class='glyphicon glyphicon-remove'></i>";
                if (smartSizing){
                    glyph = "<i class='glyphicon glyphicon-ok'></i>"
                }
                readURL(userPic);
                $('#yourInputs').append($('<tr>').append($('<p>').html(glyph + " Smart-sizing")))
                    .append($('<tr>').append($('<p>').html("Depth<br>" + depth)));
                if (smartSizing){
                    $('#yourInputs').append($('<tr>').append($('<p>').html("Threshold<br>" + thresh)));
                }
            }
        });
        e.preventDefault();
    });
});

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

    $('#mapId').on("click", "area", function(){
        //the info from the id is comma-delimited
        var info = this.id.split(",");
        showInfoBox(info[0], info[1], info[2], info[3], info[4], this);
        return false;

    });
}


$(function() {
    toggleInfoBox();
});

/*
 This function will add the img, author, and title
 to the #divInfoBox and display it.
 */

showInfoBox = function(url, author, title, id, trueUrl, area) {
    /*
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
     moveLeft = areaElement.outerWidth() + parseInt(coords[2]) + offset.left;
     moveDown = offset.top + parseInt(coords[3]) - parseInt($('#collage').attr("height"));
     ibox.show();
     ibox.offset({top: moveDown - ibox.height(), left: moveLeft});
     */
    var img = $('<img>');
    var imgLink = $('<a>').attr("href", trueUrl);
    $('#zoomedThumb').html("").append(img.attr("src", url));
    $('#title').html(title).attr({"href": trueUrl, "target":'_blank'});
    $('#author').html(author).attr({'href':"https://www.flickr.com/photos/" +  id, "target":'_blank'});
};


$(function() {
    $('#showThresh').html($('#threshIn').val());
    $('#threshIn').change( function() {
        $('#showThresh').html($('#threshIn').val());
    });
});

$(function() {
    $('#showDepth').html($('#depthIn').val());
    $('#depthIn').change(function () {
        $('#showDepth').html($('#depthIn').val());
    });
});

/*
Source: http://stackoverflow.com/questions/5802580/html-input-type-file-get-the-image-before-submitting-the-form
 */
function readURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#yourImg')
                .attr('src', e.target.result)
                .addClass('img-responsive');
        };
        reader.readAsDataURL(input.files[0]);
    }
}



$(function(){
    $("input[name=smartSizing]").change(function() {
        var dynam = document.getElementById("dynamic").checked;
        if(dynam){

            $("#depthIn").attr("max",8);
            //$("#threshSpan").show();
            $("#threshIn").attr("disabled",false);
            //$("#showThresh").show();
        }
        else{

            $("#depthIn").attr("max",7);
            //$("#threshSpan").hide();
            $("#threshIn").attr("disabled",true);
            //$("#showThresh").hide();
        }
    });
});