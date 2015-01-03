/**
* Created by nich on 1/2/15.
*/
/*
This script submits the form the build a collage with ajax.
Upon success, it will display the collage and layer it's
attribution map on top.
*/
$(function () {
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
                $('.site-wrapper-inner').hide();
                $('#mosaicContainer').show();
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


$(function() {
    toggleInfoBox();
    });

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