$(document).ready(function() {

    $(".main-dir").text($("#directory").val());
    
    $("#directory").change(function(){
    	$(".main-dir").text($("#directory").val());
    });
    
    setTimeout(function(){
    	  $('.vroom-message').remove();
    }, 5000);   

});
