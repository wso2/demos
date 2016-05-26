/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
//***********************************88888888888888888888888888888888
    setInterval(ajaxLatest, 2000);
    //ajaxLatest();
    //ajaxNews();
    //ajaxHtag("#nt-Htag2","Bernie");
    //setInterval('ajaxHtag("#nt-Htag1","Trump");', 5000);
    //ajaxHtag("#nt-Htag1","Trump");
    //setInterval('ajaxHtag("#nt-Htag2","Bernie");', 5000);
    //ajaxPopular("Trump"); 
    //setInterval('ajaxPopular("Trump");', 2000);
    ajaxGarphSentiment("js/SentimetGraphServer.jag","11","22","33");
    var ntLatest = $('#nt-latest').newsTicker({
        row_height: 80,
        max_rows: 5,
        duration: 3000,
        prevButton: $('.nt-prev'),
        nextButton: $('.nt-next')
    });

    var ntPopular = $('#nt-popular').newsTicker({
        row_height: 80,
        max_rows: 5,
        duration: 3000,
        prevButton: $('.nt-prev'),
        nextButton: $('.nt-next')
    });

    var ntNews = $('#nt-news').newsTicker({
        row_height: 80,
        max_rows: 5,
        duration: 2000,
        prevButton: $('.nt-prev'),
        nextButton: $('.nt-next')
    });
//***********************************88888888888888888888888888888888
    $("#candidate-selection-menu li").on("click", function () {
        var selectedCandidate = $(this).prop("id");

        $.ajax({
            url: "some/url/i/fancy",
            context: document.body,
            beforeSend: function () {
                $(".candidate-container").loading({
                    action: "show",
                    element: ".candidate-container",
                    loadingText: "Fetching Data..."
                });
            }
        }).done(function (data) {
            $(".candidate-container").loading({
                action: "hide"
            });
            $(".site-overlay").click();
            //console.log(data);
        }).fail(function () {

        });
    });

/*
    $.ajax({
        url: "some/url/i/fancy",
        context: document.body,
        beforeSend: function () {
            $(".trend-graph").loading({
                action: "show",
                element: ".trend-graph",
                loadingText: "Fetching Analytics Data...",
                loadIcon: "fw-fan",
                loadAnimation: "fw-spin",
            });
        },
        error:function (xhr, ajaxOptions, thrownError){
            if(xhr.status==404) {
                console.log(thrownError);
            }
        }
    }).done(function (data) {
        $(".trend-graph").loading({
            action: "hide"
        });
    }).fail(function () {

    });
    
    $.ajax({
        url: "some/url/i/fancy",
        context: document.body,
        beforeSend: function () {
            $(".sentiment-graph").loading({
                action: "show",
                element: ".sentiment-graph",
                loadingText: "Fetching Analytics Data...",
                loadIcon: "fw-fan",
                loadAnimation: "fw-spin",
            });
        },
        error:function (xhr, ajaxOptions, thrownError){
            if(xhr.status==404) {
                console.log(thrownError);
            }
        }
    }).done(function (data) {
        $(".sentiment-graph").loading({
            action: "hide"
        });
    }).fail(function () {

    });

*/
    var cardHeight = $(".card").outerHeight(true);
    $(".menu-btn").outerHeight(cardHeight);
    $(".menu-btn").css("padding-top", cardHeight / 2 - 50);

});
var ajaxLatest = function(){
        $.ajax({
            url: "js/LatestTweetserver.jag",
            dataType: "json",
            type: "POST",
            success: function(data){
                var table = $("#nt-latest");
		//console.log(data);
		//$("#nt-latest"+" li").remove();
                 //$("li").parent().prev().remove();
		      table.html(data);//***************************************
                
            },
	    error: function(e){
		alert("Error" + e);
	   }
        });
    };
//========================Yasara======================================================================

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
			alert(JSON.stringify(data));
		     text =  {
			  "width": width,
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




//======================================================================================================
(function ($) {
setInterval(ajaxLatest, 2000);
    /* ========================================================================
     * pre-loader function
     * ======================================================================== */
    $.fn.loading = function (options) {
        var settings = $.extend({
            // defaults.
            action: "show",
            element: "",
            loadIcon: "fw-wso2-logo",
            loadAnimation: "fw-pulse",
            loadingText: "Fetching Data..."
        }, options);
        var loaderString = '<div class="loader-wrapper text-center"><i class="icon fw '+settings.loadIcon +' '+settings.loadAnimation+'"></i><br/><span>'+settings.loadingText+'</span><div>';
        var loaderHeight = $(this).height();

        return $(this).each(function () {
            if (settings.action === 'show') {
                $(this).children().hide();
                $(this).prepend(loaderString).addClass('loading');
                $(".loader-wrapper").height(loaderHeight);
                $(".loader-wrapper").css("padding-top", loaderHeight / 2 - 50);
            }
            if (settings.action === 'hide') {
                $(this).children().show();
                $(".loader-wrapper").remove();
            }
        });

    };
}(jQuery));


