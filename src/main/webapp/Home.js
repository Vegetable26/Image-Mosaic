/**
 * Created by nich on 12/24/14.
 */

//this function will display a form below the current page based on the input parameter whichForm
//$(function() {

//});

function showForm(whichForm){
    var xmlhttp;
    // code for IE7+, Firefox, Chrome, Opera, Safari
    if (window.XMLHttpRequest){
        xmlhttp=new XMLHttpRequest();
    }
    // code for IE6, IE5
    else{
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState==4 && xmlhttp.status==200){
            //set the form div to have the html content defined by xmlhttp.open
            var form = document.getElementById("form");
            form.innerHTML=xmlhttp.responseText;
            var myScripts = form.getElementsByTagName("script");
            if (myScripts.length > 0) {
                for (var i = 0; i < myScripts.length; i++){
                    eval(myScripts[i].innerHTML);
                }
            }
        }
    };
    var openMe;
    if (whichForm == 1){
        openMe = "MakeCollage.jsp";
    }
    if (whichForm == 2){
        openMe = "PopulateIndex.jsp";
    }
    if (whichForm == 3){
        openMe = "CheckIndex.jsp";
    }
    xmlhttp.open("GET",openMe,true);
    xmlhttp.send();
}

//this function will display a page below the current page based on the input parameter isApproved

function showPage(isApproved){
    var xmlhttp;
    // code for IE7+, Firefox, Chrome, Opera, Safari
    if (window.XMLHttpRequest){
        xmlhttp=new XMLHttpRequest();
    }
    // code for IE6, IE5
    else{
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState==4 && xmlhttp.status==200){
            //set the content div to have the html content defined by xmlhttp.open
            var form = document.getElementById("content");
            form.innerHTML=xmlhttp.responseText;
            //get all the scripts in form
            var myScripts = form.getElementsByTagName("script");
            //if scripts exist,
            if (myScripts.length > 0) {
                //iterate over each one
                for (var i = 0; i < myScripts.length; i++){
                    //and evaluate it so that it will be written
                    eval(myScripts[i].innerHTML);
                }
            }
        }
    };
    //the jsp page to open via AJAX
    var openMe;
    //if the user is not approved, s/he can only make a collage
    if (isApproved == 0){
        openMe = "MakeCollage.jsp";
    }
    //otherwise, they can do everything
    else{
        openMe = "DoEverything.jsp";
    }
    xmlhttp.open("GET",openMe,true);
    xmlhttp.send();
}