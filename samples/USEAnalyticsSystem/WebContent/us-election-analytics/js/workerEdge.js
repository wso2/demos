

 var authenticatingString = "YWRtaW46bUxzeEFDYUg0R0VDUQ==";

 var dEdge;

 var Edges=[];

var xhr2 = new XMLHttpRequest();
xhr2.open('GET', encodeURI('https://52.77.25.83:9453/analytics/tables/ELECEDGE'));
xhr2.contentType='application/json';
xhr2.setRequestHeader('Authorization', 'Basic ' + authenticatingString);

xhr2.onload = function() {
    if (xhr2.status === 200) {
        console.log("Edge");                                                             //    TODO: for testing purpose
         dEdge=JSON.parse(xhr2.responseText);
         for(var k in dEdge) {
                  var j=dEdge[k].values;
                  Edges.push(j);
         }
         postMessage(Edges);

    }
    else {
        alert('Request failed.  Returned status of ' + xhr2.status);
    }
};
xhr2.send();
