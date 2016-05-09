var colourHTags = function() {
    var textContent = $(".cTwitterCard-body p");
    textContent.each(function() {
        $(this).html($(this).text().replace(/(@|#)\w+/g, '<span class="blue">$&</span>'));
    });
};

var numberFormat = function(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

var latestTweetList = function() {
    $.ajax({
        url: "api/LatestTweets.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            $("#latestTweets").html(data);
            colourHTags();
        },
        error: function(request, error) {
            $("#errorInLatest").html('<strong>Error!</strong> Error in latestTweetList: ' + error);
        }
    });
};


var tweepsCount = function() {
    $.ajax({
        url: "api/TweepsCount.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            $("#republicansCount").html(numberFormat(data[1]));
            $("#democratsCount").html(numberFormat(data[0]));
        },
        error: function(request, error) {
            $("#errorInTopCount").html('<strong>Error!</strong> Error in  tweepsCount: ' + error);
        }
    });
};


var popularTweetLinksList = function() {
    $.ajax({
        url: "api/PopularLinks.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            $("#popularLink").html(data);
        },
        error: function(request, error) {
            $("#errorInPopularLink").html('<strong>Error!</strong> Error in popularTweetLinksList: ' + error);
        }
    });
};

var latestGoogleNewsTicker = function() {
    $.ajax({
        url: "api/GoogleNews.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            $("#news-ticker").html(data);
        },
        error: function(request, error) {
            $("#errorInNewsTicker").html('<strong>Error!</strong> Error in latestGoogleNewsTicker: ' + error);
        }
    });
};



var popularListCandidateTag = function() {
    $.ajax({
        url: "api/PopularCandidateTweets.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            $("#popularCandidate").html(data);
        },
        error: function(request, error) {
            $("#errorInPopularCandidate").html('<strong>Error!</strong> Error in popularListCandidateTag: ' + error);
        }
    });
};

var popularListElectionTag = function() {
    $.ajax({
        url: "api/PopularElectionTweets.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            $("#popularElection").html(data);
        },
        error: function(request, error) {
            $("#errorInPopularElection").html('<strong>Error!</strong> Error in popularListElectionTag: ' + error);
        }
    });
};

var garphNewsSentiment = function() {
    var wit = $("#sentiment-graph").width();
    var hight = $("#sentiment-graph").height();
    var dateCount = 20;
    $.ajax({
        url: "api/DateCount.jag",
        dataType: "json",
        type: "GET",
        success: function(data) {
            dateCount = data;
        },
        error: function(er) {
            console.log("Error Graph GetDate" + er);
        }
    });


    $.ajax({
        url: "api/SentimetGraph.jag",
        dataType: "json",
        type: "GET",
        success: function(data, k) {
            function convertData(data) {
                var fomatedData = new Array();
                for (var i = 0; i < data.length; i++) {
                    var dataRow = new Array();
                    var d = new Date(data[i].Date + " 00:00:00");
                    dataRow.push(d.getTime());
                    dataRow.push(data[i].Candidate);
                    dataRow.push(data[i].Rate);
                    dataRow.push(data[i].News1);
                    dataRow.push(data[i].News2);
                    dataRow.push(data[i].News3);
                    fomatedData.push(dataRow);
                }

                return fomatedData;
            }
            var data = [{
                "metadata": {
                    "names": ["Date", "Candidate", "Rate", "News1", "News2", "News3"],
                    "types": ["time", "ordinal", "linear", "ordinal", "ordinal", "ordinal"]
                },
                "data": convertData(data, Number(dateCount))
            }];
            var config = {
                x: "Date",
                charts: [{
                    axesColor: "#FFFFFF",
                    titleFontColor: "#FFFFFF",
                    legendTitleColor: "#FFFFFF",
                    legendTextColor: "#FFFFFF",
                    type: "line",
                    padding: {
                        "top": 10,
                        "left": 50,
                        "bottom": 100,
                        "right": 100
                    },
                    xAxisAngle: true,
                    y: "Rate",
                    color: "Candidate",
                    colorDomain: ["TRUMP", "CLINTON", "BERNIE", "CRUZ"],
                    colorScale: ["#fa574b", "#3ec2ee", "#1c40fb", "#e51000"],
                    tooltip: {
                        "enabled": true,
                        "color": "#e5f2ff",
                        "type": "symbol",
                        "content": ["News1", "News2", "News3"],
                        "label": true
                    }
                }],
                width: wit * 0.95,
                height: hight * 0.95,
                xFormat: "%m/%d/%Y",
                xTicks: (Number(dateCount) / 3) * 2

            }
            var lineChart = new vizg(data, config);
            lineChart.draw("#sentiment-graph");

        },
        error: function(request, error) {
            $("#errorInSentiment").html('<strong>Error!</strong> Error in garphNewsSentiment: ' + error);
        }

    });


};
