// ==UserScript==
// @name       Polly Orion
// @version    0.2
// @grant 	   GM_setValue
// @grant 	   GM_getValue
// @grant 	   GM_deleteValue
// @grant 	   GM_xmlhttpRequest
// @namespace  projectpolly.de
// @require    http://code.jquery.com/jquery-1.10.2.min.js
// @include    http://www.revorix.info/*/map.php*
// @include    http://www.revorix.info/*/rx.php?set=5&fid=*
// @include    http://www.revorix.info/*/rx.php?set=6*
// ==/UserScript==


// Optionen
// Wenn true werden alle aufgerufenen Sektoren an Polly übermittelt.
var POST_SECTOR_INFORMATION = true;

// Deckt automatisch den gesamten Quadranten auf
var AUTO_UNVEIL = true;

var POLLY_ACCOUNT = "TODO";
var POLLY_PASSWORD = "TODO";



// Do not modify the following part of the script

// Setting keys
var PROPERTY_SELECTED_FLEET = "polly.orion.selectedFleet";
var PROPERTY_SELECTED_FLEET_ID = "polly.orion.selectedFleetId";
var PROPERTY_CONTROL_SHOWN = "polly.orion.controlShown";
var PROPERTY_POST_SECTOR_INFOS = "polly.orion.postSectorInfos";
var PROPERTY_AUTO_UNVEIL = "polly.orion.autoUnveil";


// Test which page has been loaded
var PAGE_CONTROL = 0;
var PAGE_SECTOR = 1;
var PAGE_NO_CONTROL = 2;
var uri = document.baseURI;
var page = PAGE_SECTOR;
if (uri.indexOf("map.php") != -1) {
    page = PAGE_SECTOR;
} else if (uri.indexOf("set=5") != -1) {
    page = PAGE_CONTROL;
} else if (uri.indexOf("set=6") != -1) {
    page = PAGE_NO_CONTROL;
} else {
    // should not be reachable
}

    


// Shows additional debugging information if set to true
var debug = true;

// API URLs
var POLLY_URL = "$host"
var SECTOR_API = "$sectorApi"
var QUADRANT_API = "$quadrantApi";
var POST_SECTOR_API = "$postSectorApi";
var IMG_URL = "http://www.revorix.info/gfx/q/";


//Variables for PAGE_SECTOR:

//contains all sectors mapped by their x and y coordinates. Valid key can 
//be obtained by the key(x,y) method.
//Entries are only valid if sectorInfosLoaded holds true
var sectorInfos = {};

//holds all img elements which have been modified due to unveiling the quad
var modifiedImgs = [];

//Whether all sectors have already been requested from the server
var sectorInfosLoaded = false;

//Whether hidden sectors are currently shown
var sectorsShown = false;

var selectedFleetName = "";
var selectedFleetId = -1;
var isFleetSelected = false;

var CHECK_FLEET_DELAY = 500;


switch (page) {
case PAGE_CONTROL:
 GM_setValue(PROPERTY_CONTROL_SHOWN, true);

 // Find out currently selected own fleet
 var table = $('table[width="100%"]')[0];
 var cell = $(table.rows[1].cells[0]).text();
 var fleetName = cell.split("\n")[0].replace(/ /g, "");

 var fleetId = findFleetId($("body").html());

 GM_setValue(PROPERTY_SELECTED_FLEET_ID, fleetId);
 GM_setValue(PROPERTY_SELECTED_FLEET, fleetName);
 break;
 
case PAGE_NO_CONTROL:
 // browsing quadrant without having a fleet selected

 GM_deleteValue(PROPERTY_SELECTED_FLEET);
 GM_deleteValue(PROPERTY_SELECTED_FLEET_ID);
 break;
 
case PAGE_SECTOR:
 // parse current page info
 var sector = parseCurrentSectorInformation();
 var fleets = parseFleetInformation();
 
 
 // Prepare GUI
 $("td[width='200']").append(
     '<p><b>Orion</b><br/><a href="#" id="showQuad">Karte aufdecken</a><a href="#" id="hideQuad">Ausblenden</a></p>');
 $("a#showQuad").hide();
 $("a#hideQuad").hide();
 $("a#showQuad").click(showQuad);
 $("a#hideQuad").click(hideQuad);
 
 if (sector.sens != "vorgescannt") {
     if (!AUTO_UNVEIL) {
         // show link to manually unscan
         $("a#showQuad").show();
     } else {
     	// this will also show the 'hide' link
         showQuad();
     }
 }
 // Add check fleet action to fleet selection links
 $('table[class="wrpd full"]').find('a[target="rxqa"]').click(function() {
     window.setTimeout(checkFleet, CHECK_FLEET_DELAY);
 });
 
 $("td[width='200']").append('<p id="pollyStatus"></p>');
 status('Aktuelle Flotte: <span id="currentFleet"></span> <a href="#" id="recheck">Update</a>');
 $("#recheck").click(checkFleet);
 checkFleet();
 window.setTimeout(checkFleet, CHECK_FLEET_DELAY);
 
 
 // Actual magic


 if (isFleetSelected) {
     fleets['currentFleet'] = selectedFleetName;
 }
 
 logObject(sector);
 logObject(fleets);
 
 // Post current sector information back to polly
 if (POST_SECTOR_INFORMATION) {
     postSectorDetails(sector);
 }
 break;
}






//Replaces all hidden sectors with information loaded from polly.
//This may request sector infos from polly if they have not already been loaded
function showQuad() {
 if (sectorsShown) {
     return;
 } else if (!sectorInfosLoaded) {
     loadQuadrant(sector.quadName, unveil);
 } else {
     unveil();
 }
}



//Hides all sectors out of sight after they have been unveiled
function hideQuad() {
 if (!sectorsShown) {
     return;
 } else {
     hide();
 }
}



//internal method for hiding all unveiled sectors
function hide() {
 $("a#showQuad").show();
 $("a#hideQuad").hide();
 var noneImg = img("u.gif");
 $.each(modifiedImgs, function(idx, img) {
     $(img).attr("src", noneImg);
 });
 sectorsShown = false;
}



//internal method for unveiling hidden sectors
function unveil() {
 $("a#showQuad").hide();
 $("a#hideQuad").show();
 
 var REGEX = /X:(\d+) Y:(\d+)/;
 $("img").each(function() {
     var ths = $(this);
     var alt = ths.attr("alt");
     var src = ths.attr("src");
     if (alt && REGEX.test(alt) && src.indexOf("u.gif") != -1) {
         var x = RegExp.$1;
         var y = RegExp.$2;
         var k = key(x, y);
         var sector = sectorInfos[k];
         if (sector) {
             modifiedImgs.push(this);
             var newSrc = img(sector.imgName);
             ths.attr("src", newSrc);
             var production = "";
             $.each(sector.production, function(idx, value) {
                 production += value.ress + ": " + value.rate.toString() + "\n";
             });
             ths.attr("title", sector.type+"\n"+
                 sector.attacker.toString()+
                 "%, "+sector.defender.toString()+
                 "%, "+sector.guard+"%".toString()+"\n\n"+
                 production);
         }
     }
 });
 sectorsShown = true;
}



//finds the last wrpd full table on the page
function lastTable() {
	var tables = $('table[class="wrpd full"]');
	if (tables.length > 0) {
 	return tables[tables.length - 1];
	}
	return null;
}



//Reads information of currently displayed fleets
function parseFleetInformation() {
 var YOUR_FLEET = "Ihre Flotte: ";
 var ALIEN_PREFIX = "Reg-Nr";
 
 var table = lastTable();
 var cell = table.rows[1].cells[0];
 var result = {
     ownFleets: [],
     fleets: []
 };
 $.each($(cell).html().split("<br>"), function(idx, row) {
     if (row == "") {
         return true;
     }
     var strippedRow = stripHtml(row);
     if (startsWith(strippedRow, YOUR_FLEET)) {
         var fleetId = findFleetId(row);
         var fleetName = strippedRow.substr(YOUR_FLEET.length);
         result.ownFleets.push({
             'fleetId': fleetId,
             'fleetName': fleetName
         });
     } else if (startsWith(strippedRow, ALIEN_PREFIX)) {
         return true;
     } else {
         var parts = strippedRow.split(" - ");
         result.fleets.push({
             name: parts[0],
             owner: parts[1]
         });
     }
 });
 return result;
}



//Reads information of the currently displayed sector and returns them as
//object
function parseCurrentSectorInformation() {
 var QUAD_INFO_REGEX = /([a-zA-Z0-9 ]+) X:(\d+) Y:(\d+)/;
 var SENS_REGEX = /Sensorstärke: (.+)/;
 var RESS_REGEX = /r(\d+)\.gif">(\d+) \((\d+(\.\d+)?)\)/;
 var WORMHOLE_REGEX = /"map_wrm\.php.+>(.+)<\/a>/;
 
 var qd = $("td[width='200']");
 var quadDetails = qd.text();
 var rows = qd.html().split("<br>");
 var result = { 
     valid: true, 
     production: [], 
     personalPortals: [],
     clanportals: []
 };
 
 var MODE_NONE = 0;
 var MODE_PERSONAL_PORTAL = 1;
 var MODE_CLAN_PROTAL = 2;
 var mode = MODE_NONE;
 
 $.each(rows, function(idx, row) {
     if (row.search(WORMHOLE_REGEX) != -1) {
         result['wormhole'] = RegExp.$1;
     }
     if (row.indexOf("txt.gif") != -1) {
         result['type'] = stripHtml(row);
     } else if (row.indexOf("a.gif") != -1) {
         var boni = stripHtml(row).replace(/%/g, "").split(" ");
         result['attacker'] = parseInt(boni[0]);
         result['defender'] = parseInt(boni[1]);
         result['guard'] = parseInt(boni[2]);
     } else if (SENS_REGEX.test(row)) {
         result['sens'] = RegExp.$1;
     } else if (QUAD_INFO_REGEX.test(row)) {
         result['quadName'] = RegExp.$1;
         result['x'] = parseInt(RegExp.$2);
         result['y'] = parseInt(RegExp.$3);
     } else if (RESS_REGEX.test(row)) {
         result.production.push({ 
             ressId: parseInt(RegExp.$1),
             rate: (RegExp.$3)
         });
     } else if (row.indexOf("Individuelles Portal") != -1) {
         mode = MODE_PERSONAL_PORTAL;
     } else if (row.indexOf("Clan Portal") != -1) {
         mode = MODE_CLAN_PROTAL;
     } else if (startsWith(row, "</td>")) {
         mode = MODE_NONE;
     } else if (mode == MODE_PERSONAL_PORTAL) {
         result.personalPortals.push(row);
     } else if (mode == MODE_CLAN_PROTAL) {
         result.clanportals.push(row);
     } else if (row != "") {
         //alert(row);
     }
 });
 result.valid = result['x'] && result['y'] && result['type'] && result['quadName'];
 return result;
}



//Sendet die Sektorinformationen an polly
function postSectorDetails(sector) {
 var postUrl = POLLY_URL+POST_SECTOR_API;
 GM_xmlhttpRequest ({
     url: postUrl,
     timeout: 5000,
     method: "POST",
     data: JSON.stringify(sector),
     onerror: function() { status("Fehler beim Senden"); },
     onload: function() { status("Daten an polly gesendet"); }
 });
}



//Fordert Sektorinformationen von polly an
function loadQuadrant(name, success) {
 var getUrl = POLLY_URL+QUADRANT_API+"?q="+name;
 GM_xmlhttpRequest ({
     url: getUrl,
     timeout: 5000,
     method: "GET",
     onerror: function(jqXHR, textStatus, errorThrown) { 
         status("Fehler beim Laden der Quadranteninfos");
     },
     onload: function(response) {
         try {
             var result = JSON.parse(response.responseText);
             $.each(result.sectors, function(idx, value) {
                 var k = key(value.x, value.y);
                 sectorInfos[k] = value;
             });
             sectorInfosLoaded = true;
             success();
             status("Quadrant " + name + " geladen");
         } catch (e) {
             status("Fehler beim Verarbeiten der Serverantwort");
         }
     }
 });
}



//(Re)checks whether a fleet is currently selected
function checkFleet() {
 selectedFleetName = GM_getValue(PROPERTY_SELECTED_FLEET);
 selectedFleetId = GM_getValue(PROPERTY_SELECTED_FLEET_ID, -1);
 var table = lastTable();
 var cell = table.rows[1].cells[0];
 var allFleets = findFleetIds($(cell).html());
 $.each(allFleets, function(idx, value) {
     isFleetSelected |= value == selectedFleetId;
     return !isFleetSelected;
 });
 $("#currentFleet").html(isFleetSelected ? selectedFleetName : "keine");
}


//HELPER functions
//logs the provided object as json to the console
function logObject(obj) {
 if (debug) {
     console.log(JSON.stringify(obj));
 }
}
//Finds the first fleet id within the provided string. Returns -1 if no fleet 
//id was found
function findFleetId(str) {
 var REGEX_FLEET_ID = /.*fid=(\d+).*/;
 if (!str) {
     return -1;
 } else if (REGEX_FLEET_ID.test(str)) {
     return parseInt(RegExp.$1);
 }
 return -1;
}
//Finds all referenced fleet ids within the provided dom element
function findFleetIds(str) {
 var REGEX_FLEET_ID = /fid=(\d+)/g;
 var ids = [];
 if (!str) {
     return ids;
 } else {
     var pattern = new RegExp(REGEX_FLEET_ID);
     var match = REGEX_FLEET_ID.exec(str);
     while (match != null) {
         ids.push(parseInt(match[1]));
         match = REGEX_FLEET_ID.exec(str);
     }
 }
 return ids;
}
//Tests whether 'str' starts with 'test'
function startsWith(str, test) {
 if (str.length < test.length) {
     return false;
 }
 return str.substr(0, test.length) == test;
}
//Shows a status message only if debug mode is activated
function debugStatus(info) {
 if (debug) {
     status(info);
 }
}
//Zeigt eine Statusmeldung an
function status(info) {
 $("#pollyStatus").append(info + "<br/>");
 console.log(info);
}
//Erstellt einen Hashmap key für einen Sektor
function key(x, y) { 
 return x + "_" + y; 
}
//Gibt den Link zu einem Sektorbildchen aus
function img(name) { 
 return IMG_URL + name; 
}
//Entfernt alle HTML Tags aus dem übergebenen String
function stripHtml(html) {
 var tmp = document.createElement("DIV");
 tmp.innerHTML = html;
 return tmp.textContent || tmp.innerText || "";
}