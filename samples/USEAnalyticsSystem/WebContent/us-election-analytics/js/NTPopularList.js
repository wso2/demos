

var ajaxLatest = function(){
        $.ajax({
            url: "js/LatestTweetserver.jag",
            dataType: "json",
            type: "POST",
            success: function(data){
                var table = $("#nt-latest");
                table.html(data);
               
             },
	    error: function(e){
		console.log("Error" + e);
	   }
        });
    };
var ajaxPopularLink = function(){
        $.ajax({
            url: "js/PopularLinkServer.jag",
            dataType: "json",
            type: "POST",
            success: function(data){
                var table = $("#nt-popularLink");
                table.html(data);
/*         
      var xxx=$('.twitter-text');
       xxx.truncate({
	alert('sss');
        'maxLines':3,
        'truncateString': '&nbsp;&#8230;',
        'showText': 'Read More',
        'hideText': 'Less',
        'tooltip': true
    })(jQuery);   */            // alert("sss");
            },
	    error: function(e){
		console.log("Error" + e);
	   }
        });
    };
var ajaxNews = function(){
        $.ajax({
            url: "js/NewsServer.jag",
            dataType: "json",
            type: "POST",
            success: function(data){
		//alert("sss");
                var table = $("#nt-news");
								str = data.toString().replace('<br><div style="padding-top:0.8em;"><img alt="" height="1" width="1"></div>','');
						str = str.toString().replace('<br><div style="padding-top:0.8em;"><img alt="" height="1" width="1"></div>','');
						str = str.toString().replace('<br><div style="padding-top:0.8em;"><img alt="" height="1" width="1"></div>','');
						str = str.toString().replace('<br><div style="padding-top:0.8em;"><img alt="" height="1" width="1"></div>','');
						str = str.toString().replace('<br><div style="padding-top:0.8em;"><img alt="" height="1" width="1"></div>','');
						str = str.toString().replace('</li>,<li>','</li><li>');		
						str = str.toString().replace('</li>,<li>','</li><li>');
						str = str.toString().replace('</li>,<li>','</li><li>');
						str = str.toString().replace('</li>,<li>','</li><li>');
						str = str.toString().replace('</li>,<li>','</li><li>');				
						var htmlObject = $(str);
						//alert(str);
                table.html(htmlObject);
            },
	    error: function(er){
		console.log("Error From News" + er);
	   }
        });
    };



var ajaxPopular = function(ChooseName){
var Candidat = { Choose : ChooseName};
        $.ajax({
            url: "js/PopularServer.jag",
            dataType: "json",
	    contentType:'application/json',
    	    data: JSON.stringify(Candidat),
            type: "POST",
            success: function(data){
		//alert(JSON.stringify(data));
                var table = $("#nt-popular");
		 table.html(data);//*************

            },
	    error: function(er){
		console.log("Error Popular Tweet" + er);
	   }
        });
   };

var PopularElection = function(ChooseName){
var Candidat = { Choose : ChooseName};
        $.ajax({
            url: "js/PopularElectionServer.jag",
            dataType: "json",
	    contentType:'application/json',
    	    data: JSON.stringify(Candidat),
            type: "POST",
            success: function(data){
		//alert("sss");
                var table = $("#nt-popularElection");
		
		 table.html(data);//*************

            },
	    error: function(er){
		console.log("Error Popular Tweet" + er);
	   }
        });
   };
/*
var ajaxGarphSentiment = function(ur,TopName,secondName,ChooseName){
var Candidates = { Choose : "BERNIE", Top : "TRUMP" , Second : "CLINTON"};
var width = $("#sentimentGrP").width();
var hight = $("#sentimentGrP").height();
var dateCount=20;
 $.ajax({   
            url: "js/DateCount.jag",
            dataType: "json",    
            type: "POST",
            success: function(data){
            //alert(data);
		//alert("hi");
	    dateCount=data;
            },
	    error: function(er){
		console.log("Error Graph GetDate" + er);
	   }
        });
   

        $.ajax({
            url: ur,
            dataType: "json",    
	    contentType:'application/json',
    	    data: JSON.stringify(Candidates),
            type: "POST",
            success: function(data){
                if(data){ 
			//alert(JSON.stringify(data));
		     text =  {
			  "width": (width*0.75),
			  "height": (hight*0.75),

			   "data": [
			   {
			     "name": "table",
			     "values":JSON.stringify(data),
			     "format": {"type": "json", "parse": {"Date": "date", "Rate": "number", "Candidate": "string"}}
			   }
			 ],
			"signals" :[{

				    "name": "hover",
				    "init": {},
				    "streams": [
					{"type": "symbol:mouseover", "expr": "datum"},
					{"type": "symbol:mouseout", "expr": "{}"}
				    ]
			    }],
			  "scales": [
		  	 {
			     "name": "x",
			     "type":  "time",
			     "range": "width",
			     "zero": false,
			     "domain": {"data": "table", "field": "Date"}
			   },
			    {
			     "name": "y",
			     "type": "linear",
			     "range": "height",
			     "nice": true,
			     "zero": true,
			     "domain": {"data": "table", "field": "Rate"}
			   }
			   ,
			    {
			      "name": "c",
			      "type": "ordinal",
			      "range": "category10",
			      "domain": {"data": "table", "field": "Candidate"}
			    }
			  ],
			   "axes": [
				   {"type": "x", "scale": "x","grid": true ,"title": "Date ","ticks":Number(dateCount),"format":"%d/%m/%Y"},
				   {"type": "y", "scale": "y","grid": true,  "title": "Sentiment Rate from Google Top News"}
				 ],
			  "legends": [
			    {"fill": "c", "title": "Candidate"}
			  ],

			"marks": [
			    {
			      "type": "group",
			      "from": {
				"data": "table",
				"transform": [{"type": "facet", "groupby": ["Candidate"]}]
			      },
			      "marks": [
					{

						    "type": "symbol",
						   
						    "properties": {
						       
						   "update": {
								  "size": {"value": 50},
								"stroke": {"value": "red"},
								"x": {"scale": "x", "field": "Date"},
							    "y": {"scale": "y", "field": "Rate"},
        							 "strokeWidth": {"value": 5}
						   },
						   "hover": {
								"size": {"value": 50},
								"stroke": {"value": "blue"}
							    }
							}

						},
				{
				  "type": "line",
				  "properties": {
				    "update": {
				      "x": {"scale": "x", "field": "Date"},
				      "y": {"scale": "y", "field": "Rate"},
				      "stroke": {"scale": "c", "field": "Candidate"},
				      "strokeWidth": {"value": 2},
                                     
				    },

				"hover": {
				  "fillOpacity": {"value": 0.5}
				}
                                   
				  }
				}
			      ]
			    }

              ,        {
            "type": "group",
            "from": {"data": "table",
                "transform": [
                    {
                        "type": "filter",
                        "test":  "datum.Date == hover.Date"
                    }
                ]},
            "properties": {
                "update": {
                    "x": {"scale": "x", "signal": "hover.Date", "offset": -5},
                    "y": {"scale": "y", "signal": "hover.Rate" , "offset": 20},
                    "width": {"value": 500},
                    "height": {"value": 50},
                    "fill": {"value": "#99CCFF"},
                    "background-color": {"value": 0.85},
                    "stroke": {"value": "#aaa"},
                    "strokeWidth": {"value": 0.5}
                }
            },

            "marks": [
 		{
                    "type": "text",
                    "properties": {
                        "update": {
                            "x": {"value": 6},
                            "y": {"value": 14},
                            "text": {"template": "{{hover.News1}}"},
                            "fill": {"value": "black"},
                            "fontWeight": {"value": "bold"}
                        }
                    }
                },                
		{
                    "type": "text",
                    "properties": {
                        "update": {
                            "x": {"value": 6},
                            "y": {"value": 24},
                            "text": {"template": "{{hover.News2}}"},
                            "fill": {"value": "black"},
                            "fontWeight": {"value": "bold"}
                        }
                    }
                },
                
                {
                    "type": "text",
                    "properties": {
                        "update": {
                            "x": {"value": 6},
                            "y": {"value": 34},
                            "text": {"template": "{{hover.News3}}"},
                            "fill": {"value": "black"},
                            "fontWeight": {"value": "bold"}
                        }
                    }
                }
            ]
        }
			  ]
			};
                     };	
		var viewUpdateFunction = (function(chart) {
		this.view = chart({el:"#sentimentGrP"}).update();
		}).bind(this);
		vg.parse.spec(text, viewUpdateFunction);
            },
	    error: function(er){
		console.log("Error Graph Sentiment" + er);
	   }
        });
    };
*/
var ajaxGarphSentiment = function(ur,TopName,secondName,ChooseName){
var Candidates = { Choose : "BERNIE", Top : "TRUMP" , Second : "CLINTON"};
var wit = $("#graph2").width();
var hight = $("#graph2").height();
var dateCount=20;
 $.ajax({   
            url: "js/DateCount.jag",
            dataType: "json",    
            type: "POST",
            success: function(data){
            //alert(data);
		//alert("hi");
	    dateCount=data;
            },
	    error: function(er){
		console.log("Error Graph GetDate" + er);
	   }
        });
   

        $.ajax({
            url: ur,
            dataType: "json",    
	    contentType:'application/json',
    	    data: JSON.stringify(Candidates),
            type: "POST",
            success: function(data,k){
          function convertData(data) {
          	var Fdata=new Array();
        	  for (var i = 0; i < data.length; i++) {
          	   var tem=new Array();
          	   var d=new Date(data[i].Date+" 00:00:00");
		 //  alert(data[i].Date);
          	   tem.push(d.getTime());
          	 //  alert(d.getTime());
          	   tem.push(data[i].Candidate);
          	   tem.push(data[i].Rate);
          	   tem.push(data[i].News1);
          	   tem.push(data[i].News2);
          	   tem.push(data[i].News3);
					Fdata.push(tem) ;
            		
        			
    			}

   		 return Fdata;
			}  	
	    //alert(convertData(data,3));
	    //alert(Number(dateCount));
	    var data =  [
                          { 
                            "metadata" : {
                                "names" : ["Date","Candidate","Rate", "News1","News2","News3"],
                                "types" : ["time","ordinal","linear","ordinal","ordinal","ordinal"]
                            },
                            "data": convertData(data,Number(dateCount))
                          }
                        ];

                        var config = {
                          x : "Date",
                          charts : [
                            {type: "line",padding: {"top": 10, "left": 50, "bottom": 100, "right": 100},xAxisAngle:true, y : "Rate", color: "Candidate",colorDomain:["TRUMP","CLINTON","BERNIE","CRUZ"], colorScale:["#ffcc00","#00e6e6","#d966ff","#ace600"],tooltip: {"enabled":true, "color":"#e5f2ff", "type":"symbol", "content":["News1","News2","News3"], "label":true}}
                          ],
                          width: wit,
                          height: hight*0.95,
                          xFormat:"%d/%m/%Y",
			  xTicks:Number(dateCount)-2
								  
                        }

                        var lineChart = new vizg(data, config);
			//console.log(lineChart.getSpec());
                        lineChart.draw("#graph2");
		 	console.log(lineChart.getSpec());

                     }

        });

    };




