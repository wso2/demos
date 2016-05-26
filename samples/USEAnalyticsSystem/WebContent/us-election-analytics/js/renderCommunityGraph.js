/*
*Function to  Render the community Graph
*/

function drawCommunityGraph(divId,graph){

     var divID ="#"+divId;
     console.log(divID);
     d3.select(divID).select("svg").remove();
     var width = $(divID).width();
     var height = $(divID).height() ;
     console.log("width"+ width);
     console.log("height"+height);

     /*
     *Function to assign svg for the graph
     */
     var svg = d3.select(divID).append('svg')
                 .attr("width", width)
                 .attr("height", height);


     /*
     * Function for the zooming effect
     */
    // var zoom = d3.behavior.zoom().scaleExtent([1, 8]).on("zoom", zoomed);

     /*
      *Function to assign tool tip with the label
      */
     var tip = d3.tip()
                 .attr('class', 'd3-tip')
                 .offset([-10, 0])
                 .html(function (d) {
                    return d.name + "";
                  })
           svg.call(tip);

     console.log("drap graph");
     svg.selectAll(".link").remove();
     svg.selectAll(".gnode").remove();
     var radius =80;

      /*
      *Function to give a force
      */
     var  force = self.force = d3.layout.force()
                            .nodes(graph.nodes)
                            .links(graph.links)
                            .gravity(.05)
                            .distance(60)
                            .charge(-120)
                            .size([width, height])
                            .start();
     setTimeout(function(){force.stop();},2000);

     /*
     *Function to map radius  domain--> range
     */
     var rScale = d3.scale.linear()
                    .domain([d3.min(graph.nodes, function (d) {
                               return (d.degree);
                    }),
                  d3.max(graph.nodes, function (d) {
                               return (d.degree);
                    })])
                   .range([6, radius]);
     var topdegree= d3.max(graph.nodes, function (d) {
                               return (d.degree);
                    });

     /*
     *Function to create Link
     */
     var link = svg.selectAll(".link")
                  .data(graph.links)
                  .enter().append("line")
                  .attr("class", "link")
                  .style("stroke-width", 2)
                  .style("stroke-length", 150);
                                                //TODO:function (d) {return (d.value * 10); });// 2 * Math.sqrt(d.value)

    /*
    *Function to create Node
    */
    var node = svg.selectAll(".gnode")
                   .data(graph.nodes)
                   .enter().append("g")
                   .attr("class", "gnode")
                   .on( 'click', function(d){
                        var url = "https://twitter.com/"+d.name+"/profile_image?size=original";
                   } )
                   .call(force.drag);

    /*
    * Function to add a circle to a node
    */
      var circle = node.append("circle")
                       .attr("r", function (d) {
                            return rScale(d.degree);
                       })
                       .style("fill", function (d) {
                             return d.color;


                       })
                       .style("fill-opacity", 0.7)
                       .style("stroke",function (d) {
                               return d.color;

                       })
                       .on('mouseover', tip.show)
                       .on('mouseout', tip.hide)
                       .call(force.drag);

      /*
      * Function to add a label to a circle
      */
     var label = node.append("text")
                     .style("font-family", "sans-serif")
                     .style("text-anchor", "middle")
                     .style("font-size",function (d) {
                             return (Math.log10(d.degree)*2);
                     })
     				 .style("fill", function (d) {
                                    return "#000000";

                      })
                     .text(function (d) {
                           if(d.degree>2000){
                                 var str=d.name;
                                 return  str;
                           }

                      });

     /*
     *Function to force draw the graph
     */
     force.on("tick", function () {

               circle.attr("cx", function (d) {
                                             return d.x = Math.max(radius/2, Math.min(width - radius/2, d.x))
                        })
                     .attr("cy", function (d) {
                                             return d.y = Math.max(radius/2, Math.min(height - radius/2, d.y));
                       });
                //   circle.each(collide(0.5));
               label.attr("x", function (d) {
                            return d.x;
                        })
                    .attr("y", function (d) {
                             return d.y;
                        });


               link.attr("x1", function (d) {
                              return d.source.x;
                      })
                   .attr("y1", function (d) {
                              return d.source.y;
                      })
                   .attr("x2", function (d) {
                              return d.target.x;
                      })
                   .attr("y2", function (d) {
                              return d.target.y;
                        });



      });




}