$(document).ready(function() {
    $.ajax({
//        url: "http://ftvlogger:8080/ftvRESTful/getLastFtvData",  //set me for release
        url: "http://localhost:8080/ftvRESTful/getLastFtvData",    //set me for devel
        headers: {'Access-Control-Allow-Origin': '*' }
    }).then(function(data) {
       $('.greeting-id').append(data.ftv_energy);
       $('.greeting-content').append(data.con_energy);
    });
});