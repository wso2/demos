

 var authenticatingString = "YWRtaW46dXNlckBkYXM=";

 var dNode,dEdge;
 var graph = new Object(); var map = new Object();
 var index = 0;
 var linkIndex = 0;
 var Nodes=[],Edges=[];

var xhr2 = new XMLHttpRequest();
xhr2.open('GET', encodeURI('https://552.71.156.175:9451/analytics/tables/ELECEDGE'));
xhr2.contentType='application/json';
xhr2.setRequestHeader('Authorization', 'Basic ' + authenticatingString);

xhr2.onload = function() {
    if (xhr2.status === 200) {
        console.log("communitygraph");
       // var str ='User\'s name is ' + xhr2.responseText;
       //  console.log(str);                                                               //    TODO: for testing purpose
         dEdge=JSON.parse(xhr2.responseText);
         requestNode();

    }
    else {
        alert('Request failed.  Returned status of ' + xhr2.status);
    }
};
xhr2.send();

function requestNode(){
    var authenticatingString = "YWRtaW46dXNlckBkYXM=";                               //TODO :window.btoa("admin:user@das");

    var xhr1 = new XMLHttpRequest();
    xhr1.open('GET', encodeURI('https://52.71.156.175:9451/analytics/tables/ELECNODE'));
    xhr1.contentType='application/json';
    xhr1.setRequestHeader('Authorization', 'Basic ' + authenticatingString);

    xhr1.onload = function() {
        if (xhr1.status === 200) {
         //   var str ='User\'s name is ' + xhr1.responseText;
           // console.log(str);                                                                   //TODO: for testing purpose
             dNode=JSON.parse(xhr1.responseText);
            // console.log(JSON.stringify(dNode));
             loadNodeData(Nodes,dNode);
                    //  console.log(Nodes);
             loadEdgesData(Edges,dEdge);
                     //console.log("Edges:"+ Edges);
             renderNodesEdges(Nodes,Edges);
        }
        else {
            alert('Request failed.  Returned status of ' + xhr1.status);
        }
    };
    xhr1.send();

}


    function loadNodeData(Nodes,dNode){
                for(var k in dNode) {

                    var j=dNode[k].values;
                    console.log(j);
                    Nodes.push(j);
                }

             //  console.log("Nodes "+Nodes);


     }

     function loadEdgesData(Edges,dEdge){
              for(var k in dEdge) {
                      var j=dEdge[k].values;
                      Edges.push(j);
               }
             // console.log("Nodes "+Edges);


     }


function renderNodesEdges(dataset1,dataset2){
              console.log("render graph");
                dataset1.forEach(function (d) {
                   // alert(d.name);
                   map[d.name] = index;
                   d.degree = parseInt(d.degree)
                   index++;
               });

               graph.nodes = dataset1;
            //  tlinks = new Object();
               dataset2.forEach(function (d) {

                   /*
                    Data Format Edge
                    ================
                    source
                    target
                    value

                    Data Format Vertex
                    ================
                    name
                    group
                    degree - decide size of the vertex

                    */

                  var s = map[d.source];
                   var t = map[d.target];

                   if (typeof  s === "undefined" || typeof  t === "undefined") {
                       d.source = 1
                       d.target = 2;

                   } else {
                       d.source = map[d.source];
                       d.target = map[d.target];
                       d.value = parseInt(d.value)
                   }
               });

            graph.links = dataset2;
            //console.log(graph);
            postMessage(graph);


}