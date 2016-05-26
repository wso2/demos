var authenticatingString = "YWRtaW46bUxzeEFDYUg0R0VDUQ==";                               
var data;
var xhreq = new XMLHttpRequest();
xhreq.open('GET', encodeURI('https://52.207.239.223:9451/analytics/tables/PARTYUSERCOUNT'));

xhreq.contentType='application/json';
xhreq.setRequestHeader('Authorization', 'Basic ' + authenticatingString);
	

xhreq.onload = function() {

    if (xhreq.status == 200) {
         data=JSON.parse(xhreq.responseText);
console.log(data);         enterVal();
    }
    else {
        console.log('Request failed.  Returned status of ' + xhr1.status);
    }
};
xhreq.send();

function  enterVal(){
    var j = 1,demo=0,rep=0;
    var divID1="#div1";
    var divID2="#div2";


                var arrayC = new Array();

                for (var i = 0; i < data.length; i++) {
		    
                    var partyName = data[i].values.party;
		    var userCnt = data[i].values.userCount;

                    if((partyName=="TRUMP") || (partyName=="CRUZ")){                   
                    rep+=userCnt;
	            }

		    if((partyName=="BERNIE")|| (partyName=="CLINTON")){
                    demo+=userCnt;
		    }

                }
		
		var jsonObj = new Object();
		jsonObj.divID1 = divID1;
		jsonObj.divID2 = divID2;
		jsonObj.rep = rep;
		jsonObj.demo = demo;

                arrayC.push(jsonObj);
                var jsonArray1 = JSON.parse(JSON.stringify(arrayC))
                console.log(JSON.stringify(jsonArray1));
               postMessage(jsonArray1);

}
