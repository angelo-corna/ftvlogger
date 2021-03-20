$(document).ready(function() {
    $.ajax({
//        url: "https://ftvlogger:8443/ftvRESTful/getLastFtvData",  //set me for release
    	  url: "http://localhost:8080/ftvRESTful/getLastFtvData",    //set me for devel
        headers: {'Access-Control-Allow-Origin': '*' }
    }).then(function(data) {
       $('.ftvDate').append(new Date(parseInt(data.date)));
       $('.ftvCon').append(data.con_energy);
    });
});