    // Document on load.
    $(function() {
        var newColour = ["#fa574b", "#3ec2ee", "#e51000", "#1c40fb"];
        tweepsCount();      
        popularListCandidateTag();
        popularListElectionTag();
        popularTweetLinksList();
        latestGoogleNewsTicker();
        latestTweetList();
        getCandidateCloud();
        setInterval(latestTweetList, 5000);		
        setInterval(tweepsCount, 1000);
        garphNewsSentiment();
        drawCommunityGraph("communityGraph");

    });
