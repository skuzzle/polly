// ==UserScript==
// @name        revorix.de UmgebungslistenScript
// @namespace   skuzzle.de
// @author      smash,betty
// @description Script um die Revorix-Umgebungsliste an den Polly-Bot zu schicken
// @include     http://www.revorix.info/php/venad_list.php?pktsur=1
// @include     http://www.revorix.info/php/venad_list.php?pkttop=1
// @version     1.0
// ==/UserScript==

var pollyUserName = "rxScoreBoardPaster"
var pollyPassword = "TODO"
var pollyUrl = "$host";
var pollyAPI = "/api/postScoreboard";
var verbose = true; // prints status message in table header


// run the script

GM_log("running....");
if (document.baseURI.indexOf("pktsur=1") != -1) {
    GM_log("chosing environment");
    sendData(getData());
} else {
    GM_log("chosing top50");
    sendData(getTop50Data());
}



function getData() {
    var tableData = document.getElementsByTagName("td");
    var postData = "";

    var start = 4; // skip header
    var length = tableData.length - 1; // skip footer
    
    for (var i = start; i <= length; i++) {       
        if (tableData[i].firstChild.tagName == 'A') { //venad with profile and clantag
            postData += tableData[i].firstChild.textContent;
            if (tableData[i].firstChild.nextSibling) { //venad with profile without clantag
                postData += tableData[i].firstChild.nextSibling.textContent;
            }
        } else { //venad without profile and without clantag
            postData += tableData[i].firstChild.textContent;
        }

        if (i%3 == 0) {
            postData += "\n";
        } else {
            postData += " ";
        }
    }
    return postData;
}



function getTop50Data() {
    var tableData = document.getElementsByTagName("td");
    var postData = "";
    
    var start = 5; // skip header
    var length = tableData.length - 1; // skip footer
    
    var col = 0;
    for (var i = start; i <= length; i++) {
        if (col == 2) {
            col++;
            continue; // skip "Titel" column;
        }
        
        if (tableData[i].firstChild.tagName == 'A') { //venad with profile and clantag
            postData += tableData[i].firstChild.textContent;
            if (tableData[i].firstChild.nextSibling) { //venad with profile without clantag
                postData += tableData[i].firstChild.nextSibling.textContent;
            }
        } else { //venad without profile and without clantag
            postData += tableData[i].firstChild.textContent;
        }
        
        if (col++ == 3) {
            postData += "\n";
            col = 0;
        } else {
            postData += " ";
        }
    }
    return postData;
}



function sendData(postData) {
    GM_log(postData);
    printStatus("Sending data...");
    GM_xmlhttpRequest({
        timeout: 25000,
        method: "POST",
        url: pollyUrl + pollyAPI,
        data: "user="+pollyUserName + "&pw="+pollyPassword + "&paste=" + encodeURI(postData),
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        onload: function(response) {
            var result = JSON.parse(response.responseText);
            printStatus(result.message);
        },
        ontimeout: function(response) {
            printStatus("Timeout while sending data!");
            if (response.responseText.indexOf("Success!") == -1) {
                printStatus("Error while sending data");
            }
        }
    });    
}



function printStatus(status) {
    if (!verbose) {
        return;
    }
    var elements = document.getElementsByTagName("td");
    var first = elements[0];
    first.innerHTML = status;
}