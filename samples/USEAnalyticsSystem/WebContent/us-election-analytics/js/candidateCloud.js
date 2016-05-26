
/*************************************************************************************************************
*  Common WordCloud
***************************************************************************************************************/

 var stopWords ="(a|b| ef)";

 var textData="";
 var authenticateString = window.btoa("admin:mLsxACaH4GECQ");
 //"10.100.4.185"; //"52.77.25.83";


function drawPersonWordCloud( cloudDiv ,Pname ,color){



   // var tableName= Cname+"WORDCLOUD";
    var j=1;var i;

    var stopWords ="(goodnight|nul|null)";
alert('incloud');
        getPersonDataCloud( cloudDiv,Pname ,color );
          setInterval(function() {
                              // Do something every 5 minutes
                           //   getPersonDataCloud( cloudDiv,Pname ,color );

                        }, 30000);




}




function updatePersonText( new_text, stopWords, cloudDiv,color){
        var cloudDivID ="#"+cloudDiv;
            var width = $(cloudDivID).width();
           // var widthDid = $("news").width();
            var height = $(cloudDivID).height();

            var  colorset =[color,"#A19A9A","#000000"];
            //console.log(width);
                var text={
                                  "width":width,
                                  "height": height,
                                  "padding": {"top":0, "bottom":0, "left":0, "right":0},

                                  "data": [
                                    {
                                      "name": "table",
                                      "values": [ new_text
                                          ],

                                      "transform": [
                                        {
                                          "type": "countpattern",
                                          "field": "data",
                                          "case": "upper",
                                          "pattern": "[\\w']{3,}",
                                          "stopwords": stopWords
                                        },
                                        {
                                          "type": "formula", "field": "angle",
                                          "expr": "[-45, 0, 45][~~(random() * 3)]"
                                        },
                                        {
                                          "type": "formula", "field": "weight",
                                          "expr": "if(datum.text=='VEGA', 600, 300)"
                                        },
                                        {
                                          "type": "wordcloud",
                                          "size": [width, height],
                                          "text": {"field": "text"},
                                          "rotate": {"field": "angle"},
                                          "font": {"value": "Verdana"},
                                          "fontSize": {"field": "count"},
                                          "fontWeight": {"field": "weight"},
                                          "fontScale": [20,60]
                                        }
                                      ]
                                    }
                                  ],

                                  "scales": [
                                    {
                                      "name": "color",
                                      "type": "ordinal",
                                      "range": colorset
                                    }
                                  ],

                                  "marks": [
                                    {
                                      "type": "text",
                                      "from": {"data": "table"},
                                      "properties": {
                                        "enter": {
                                          "x": {"field": "layout_x"},
                                          "y": {"field": "layout_y"},
                                          "angle": {"field": "layout_rotate"},
                                          "font": {"field": "layout_font"},
                                          "fontSize": {"field": "layout_fontSize"},
                                          "fontStyle": {"field": "layout_fontStyle"},
                                          "fontWeight": {"field": "layout_fontWeight"},
                                          "text": {"field": "text"},
                                          "align": {"value": "center"},
                                          "baseline": {"value": "alphabetic"},
                                          "fill": {"scale": "color", "field": "text"}
                                        },
                                        "update": {
                                          "fillOpacity": {"value": 1}
                                        },
                                        "hover": {
                                          "fillOpacity": {"value": 0.5}
                                        }
                                      }
                                    }
                                  ]

                     };


            return text;

}

function getPersonDataCloud(cloudDiv, Pname, color) {
    var cloudDivID = "#" + cloudDiv;
    var newTestString = " ";
    var Candidates = {
        Choose: Pname
    };
    $.ajax({
        url: "js/candidateCloud.jag",
        dataType: "json",
        contentType: 'application/json',
        data: JSON.stringify(Candidates),
        type: "POST",
        success: function(data) {
            //    console.log(TextData);
            var TextData = JSON.stringify(data);
//           alert(data);
            var res = TextData.split(";");
            var longstr = "";

            for (var i = 1; i < res.length - 1; i++) {
                var row = res[i].split(",");
               // alert(res);
		
		//alert(res[i]);
                var word = row[0].split(":");
                var count = row[1].split(":");
		//alert(word[0]);	
               if(!(word[1]==' nul')){
	      // alert(word.valueOf());
                for (var j = 0; j < count[1]; j++) {
                    longstr += word[1] + " ";

                }
		}

            }

            new_cloud = updatePersonText(longstr, stopWords, cloudDiv, color);
            //  console.log(JSON.stringify(new_cloud));
            var viewUpdateFunction = (function(chart) {
                this.view = chart({
                    el: cloudDivID
                }).update();
            }).bind(this);
            vg.parse.spec(new_cloud, viewUpdateFunction);


        }

    });



};
function getTrumpDataCloud(cloudDiv, Pname, color) {
    var cloudDivID = "#" + cloudDiv;
    var newTestString = " ";
    var Candidates = {
        Choose: Pname
    };
    $.ajax({
        url: "js/candidateCloudTrump.jag",
        dataType: "json",
        contentType: 'application/json',
        data: JSON.stringify(Candidates),
        type: "POST",
        success: function(data) {
             console.log("kkkkk");
            var TextData = JSON.stringify(data);
           console.log(TextData);
            var res = TextData.split(";");
            var longstr = "";

            for (var i = 1; i < res.length - 1; i++) {
                var row = res[i].split(",");
               // alert(res);

                //alert(res[i]);
                var word = row[0].split(":");
                var count = row[1].split(":");
                //alert(word[0]);
               if(!(word[1]==' nul')){
              // alert(word.valueOf());
                for (var j = 0; j < count[1]; j++) {
                    longstr += word[1] + " ";

                }
                }

            }

            new_cloud = updatePersonText(longstr, stopWords, cloudDiv, color);
            //  console.log(JSON.stringify(new_cloud));
            var viewUpdateFunction = (function(chart) {
                this.view = chart({
                    el: cloudDivID
                }).update();
            }).bind(this);
            vg.parse.spec(new_cloud, viewUpdateFunction);


        }

    });


};
