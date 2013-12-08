// ==UserScript==
// @name        revorix.de Fleet scan
// @namespace   qzone.servebbs.net
// @author      smash,betty, qzone
// @description Script um einen Revorix-Flottenscan an den Polly-Bot zu schicken
// @include     http://www.revorix.info/php/map_fflotte.php?fid=*
// @include		http://www.revorix.info/php/map.php?fid=*
// @grant       GM_setValue
// @grant       GM_getValue
// @grant       GM_xmlhttpRequest
// @grant       GM_log
// @version     0.3
// @updateURL   http://qzone.servebbs.net/gm/fleetscan.user.js
// @downloadURL http://qzone.servebbs.net/gm/fleetscan.user.js
// ==/UserScript==

var verbose = true; // prints status message in table header
var consolelog = false; // log messages in console

var pollyUserName = "rxScoreBoardPaster";
var pollyPassword = "TODO";
var pollyUrl = "$host";
var pollyApi = "$api";
var simulate = false;

function log(message) {
	if(consolelog) {
		GM_log(message);
	}
}

function getFleetPosition() {   ///html/body/div[2]/div[2]/div/table/tbody/tr/td[2]
	var node = getElementByXPath("/html/body/div[2]/div[2]/div/table/tbody/tr/td[2]");
	var position = node.firstChild.textContent;
	GM_setValue('fleetPosition', position);
}

function getShips() {
	var node = null;
	node = getElementByXPath("/html/body/table[3]");
    return node.textContent;;
}

function getSensors() {
	var node = null;
	node = getElementByXPath("/html/body/table/tbody/tr[2]/td");
    return node.firstChild.textContent + "\n";
}

function getLeader() {
	var node = null;         //html/body/table[2]/tbody
	node = getElementByXPath("/html/body/table[2]/tbody");
	var fleetNameAndLeader = node.childNodes[2].textContent;
	var fleetTag = node.childNodes[3].textContent;
    return fleetNameAndLeader + "\n" + fleetTag;
}

function getData() {
	var data = GM_getValue('fleetPosition') + "\n";
	data = data + getSensors();
	data = data + getLeader();
	data = data + getShips();
	return data;
}

function sendData(postData) {
    log(postData);
    printStatus("Sending data...");
    if(simulate) {
    	return;
    }
    GM_xmlhttpRequest({
        timeout: 15000,
        method: "POST",
        url: pollyUrl + pollyApi,
        data: "user="+pollyUserName + "&pw="+pollyPassword + "&scan=" + encodeURI(postData),
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        onload: function(response) {
        	//It seems like this Battlereport already exists
        	var result = JSON.parse(response.responseText);
    		printStatus(result.message)
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

function printStatus(status) {
    if (!verbose) {
        return;
    }
    if(status != "") {
    	status = " - " + status;
    }
    var node = getElementByXPath("/html/body/table[2]/tbody/tr/td");
    try {
	    if(node.innerHTML.indexOf("Flotten Daten") != -1) {
    		node.innerHTML = "Flotten Daten" + status;
    	}
    } catch(e) {
   		;
   	}
}

// run the script
if (document.baseURI.indexOf("map.php") != -1) {
	getFleetPosition();
} else {
	sendData(getData());
}
