
    // Document on load.
    $(function() {

 var newColour=["#fa574b","#3ec2ee","#e51000","#1c40fb"];
	ajaxCountD();
	ajaxCountR(); 
	setInterval(ajaxCountD, 1000);
        setInterval(ajaxCountR, 1000);
        ajaxPopular("Trump");
        PopularElection("Trump");
        ajaxPopularLink();
        ajaxNews();
        getPersonDataCloud("WCR1","TRUMP",newColour[0] );
        getPersonDataCloud("WCD1","CLINTON",newColour[1] );
        getPersonDataCloud("WCD2","BERNIE",newColour[3] );
       // getPersonDataCloud("WCR2","CRUZ",newColour[2] );

        setInterval(ajaxLatest, 5000);
        ajaxGarphSentiment("js/SentimetGraphServer.jag","11","22","33");
	drawCommunityGraph("communityGraph");


       // setInterval('ajaxPopular("Trump");', 100000);
       // setInterval('PopularElection("Trump");',100000);
       // setInterval(ajaxPopularLink, 8000);

    });

