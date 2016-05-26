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
		alert("Error" + e);
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

            },
	    error: function(e){
		alert("Error" + e);
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
		$("#nt-news"+" li").remove();
                if(data){
                    $.each(data,function(i,e){
                        table.append("<li>"+e+"</li>");
                    });
                }
            },
	    error: function(er){
		alert("Error From News" + er);
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
		//alert("sss");
                var table = $("#nt-popular");
		 table.html(data);//*************

            },
	    error: function(er){
		alert("Error Popular Tweet" + er);
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
		alert("Error Popular Tweet" + er);
	   }
        });
   };
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
		alert("Error Graph GetDate" + er);
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
				   {"type": "x", "scale": "x","grid": true ,"title": "Date ","ticks":Number(dateCount)},
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
		alert("Error Graph Sentiment" + er);
	   }
        });
    };



