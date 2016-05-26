/*
*Getting top candidates
*/

var authenticatingString = "YWRtaW46bUxzeEFDYUg0R0VDUQ==";                               //TODO :window.btoa("admin:user@das");
var data1,data2;
var xhr1 = new XMLHttpRequest();
xhr1.open('GET', encodeURI('https://52.207.239.223:9451/analytics/tables/REPUBLICANS'));

xhr1.contentType='application/json';
xhr1.setRequestHeader('Authorization', 'Basic ' + authenticatingString);

xhr1.onload = function() {
    if (xhr1.status === 200) {
//console.log("=================fffffffffffffffffffffffffffffffffff====="+xhr1.status);        //var str ='User\'s name is ' + xhr1.responseText;
        //postMessage(str);                                                                  
         data1=JSON.parse(xhr1.responseText);
//console.log(data1);  
      sendSecondAjax();console.log(data1); 

    }
    else {//console.log("======================"+xhr1.status+"sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
        console.log('Request failed.  Returned status of ' + xhr1.status);
    }
};
xhr1.send();

function sendSecondAjax(){
        var xhr2 = new XMLHttpRequest();
        xhr2.open('GET', encodeURI('https://52.207.239.223:9451/analytics/tables/DEMOCRATES'));
        xhr2.contentType='application/json';
        xhr2.setRequestHeader('Authorization', 'Basic ' + authenticatingString);

        xhr2.onload = function() {
                if (xhr2.status === 200) {
                                                                                // TODO: for testing purpose
                 data2=JSON.parse(xhr2.responseText);
                putValues();
       
                         }
          else {
                         console.log('Request failed.  Returned status of ' + xhr2.status);
                }
         };
        xhr2.send();


}

function putValues(){
    var j = 1;
//alert("kkk");
                var array = new Array();

                for (var i = 0; i < 2; i++) {

                    var nameID = "#cCandidateNameR" + (j);
                    var imageID = "#cimgR" + (j);
                    var countID = "#cCandidateNumbersR" + (j);
                    var cloudDiv = "WCR" + (j);
                    var ImgUrl=data1[i].values.imageUrl;
                    var retweet=data1[i].values.retweet;
                    var fullName=data1[i].values.fullName;
                    var party=data1[i].values.party;
                    var color= data1[i].values.color;

                    var jsonOb = new Object();;
                    jsonOb.nameID = nameID;
                    jsonOb.imageID = imageID;
                    jsonOb.countID = countID;
                    jsonOb.cloudDiv = cloudDiv;
                    jsonOb.ImgUrl=ImgUrl;
                    jsonOb.retweet=retweet;
                    jsonOb.fullName=fullName;
                    jsonOb.party=party;
                    jsonOb.color=color;

                    array.push(jsonOb);
                    var nameID2 = "#cCandidateNameD" + (j);
                    var imageID2 = "#cimgD" + (j);
                    var countID2 = "#cCandidateNumbersD" + (j);
                    var cloudDiv2 = "WCD" + (j);

                    var ImgUrl2=data2[i].values.imageUrl;
                    var retweet2=data2[i].values.retweet;
                    var fullName2=data2[i].values.fullName;
                    var party2=data2[i].values.party;
                    var color2= data2[i].values.color;
                    j++;
                    var jsonOb2 = new Object();
                    jsonOb2.nameID = nameID2;
                    jsonOb2.imageID = imageID2;
                    jsonOb2.countID = countID2;
                    jsonOb2.cloudDiv = cloudDiv2;
                    jsonOb2.ImgUrl=ImgUrl2;
                    jsonOb2.retweet=retweet2;
                    jsonOb2.fullName=fullName2;
                    jsonOb2.party=party2;
                    jsonOb2.color=color2;

                    array.push(jsonOb2);
                }
                var jsonArray = JSON.parse(JSON.stringify(array))
                console.log(JSON.stringify(jsonArray));
                postMessage(jsonArray);



}
