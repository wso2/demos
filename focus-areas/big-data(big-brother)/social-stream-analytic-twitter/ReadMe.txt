Please follow below instructions to run the sample

* Unzip BAM 2.4.0
* Replace the folders in the CEP_Artefacts.zip to the folders of <BAM_HOME>/repository/deployment/server/
* Open the file <BAM_HOME>/repository/deployment/server/eventformatters/EmailRetweetEventFormatter.xml and replace “xxx@wso2.com” with your email address.
* Start the BAM server
* Deploy Social_Stream_Analysis.tbox
* Run the Java Client (TwitterAgent.zip) to publish twitter stream data. 

You will be able so see twitter stream data analysis in the WSO2 BAM Gadget Portal and also get notifications when there is a retweet.