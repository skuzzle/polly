// ==UserScript==
// @name        revorix.de KB Report
// @namespace   qzone.servebbs.net
// @author      smash,betty, qzone
// @description Script um die Revorix-Kampfberichte an den Polly-Bot zu schicken
// @include     http://www.revorix.info/php/news_pop.php*
// @include		http://www.revorix.info/php/map_attack.php?fida=*
// @grant       GM_xmlhttpRequest
// @grant       GM_log
// @version     0.4
// ==/UserScript==

var verbose = true; // prints status message in table header
var consolelog = false; // log messages in console

var pollyUserName = "rxScoreBoardPaster";
var pollyPassword = "TODO";
var pollyUrl = "$host";
var pollyApi = "$api";
var isLiveKb = false;
var sendTimeout = 10000;

function log(message) {
	if(consolelog) {
		GM_log(message);
	}
}

function getData() {
	var tableNode = null;
	if(isLiveKb) {
    	tableNode = getElementByXPath("/html/body/div[2]/div[2]/div/table/tbody");
    } else {
    	tableNode = getElementByXPath("/html/body/table/tbody");
    }
    var postData = tableNode.textContent;
    return postData;
}

function sendData(postData) {
    log(postData);
    printStatus("Sending data...");
    GM_xmlhttpRequest({
        timeout: sendTimeout,
        method: "POST",
        url: pollyUrl + pollyApi,
        data: "user="+pollyUserName + "&pw="+pollyPassword + "&report=" + encodeURI(postData) + "&isLive="+isLiveKb,
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        onload: function(response) {
        	//It seems like this Battlereport already exists
            var result = JSON.parse(response.responseText);
            printStatus(result.message);
            if (result.lowPzWarning) {
            	alert(result.lowPzShips);
            }
        },
        ontimeout: function(response) {
            printStatus("Timeout while sending data!");
            log("Timeout while sending data!");
        }
    }); 
}

function getElementByXPath(path) {
  result = document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
  return result.singleNodeValue;
}

function isKB() {
	// kb of stored message
    var node = getElementByXPath("/html/body/table/tbody/tr[2]/td/table/tbody/tr/td");
    try {
	    if(node.innerHTML.indexOf("Zurückgelassene Ressourcen") != -1) {
    		return true;
    	}
    } catch(e) {
		// live kb
    	node = getElementByXPath("/html/body/div[2]/div[2]/div/table/tbody/tr/td/table/tbody/tr/td");
    	try {
    		if(node.innerHTML.indexOf("Zurückgelassene Ressourcen") != -1) {
    			isLiveKb = true;
    			return true;
    		}
    	} catch(e) {
    		return false;
    	}
    }
    return false;
}
    	

function printStatus(status) {
    if (!verbose) {
        return;
    }
    if(status != "") {
    	status = " - " + status;
    }
    var node = getElementByXPath("/html/body/table/tbody/tr[2]/td/table/tbody/tr/td");
    try {
	    if(node.innerHTML.indexOf("Zurückgelassene Ressourcen") != -1) {
    		node.innerHTML = "Zurückgelassene Ressourcen" + status;
    	}
    } catch(e) {
		// live kb
    	node = getElementByXPath("/html/body/div[2]/div[2]/div/table/tbody/tr/td/table/tbody/tr/td");
    	try {
    		if(node.innerHTML.indexOf("Zurückgelassene Ressourcen") != -1) {
    			node.innerHTML = "Zurückgelassene Ressourcen" + status;
    		}
    	} catch(e) {
    		;
    	}
    }
}

// run the script
if(isKB()) {
	sendData(getData());
}
