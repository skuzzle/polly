#parse ( "/polly/rx/httpv2/view/orionv2.meta.js" )



// Your User Data
// Venad will be determined automatically.  With this setting you can override
// it permanently. Set to "" for using auto determined venad
var VENAD_OVERRIDE  = "";

// Clan tag is needed for sharing own fleet position.
var CLAN_TAG        = "[Loki]";

// Login credentials for polly. WARNING: name and password are stored and sent
// unencrypted!
var POLLY_USER_NAME = "";
var POLLY_PASSWORD  = "";



// Features. Settings these to false will disable the corresponding feature
// completely. Most features offer additional settings using the user interface
// when enabled
var FEATURE_ALL               = true;     // turns off all features completely when set to false
var FEATURE_LOGIN_INTEGRATION = true;     // login code sharing. WARNING: if you turn this off, VENAD_OVERRIDE has to be set manually
var FEATURE_MAP_INTEGRATION   = true;     // unveiling map, sending fleet and sector data
var FEATURE_NEWS_INTEGRATION  = true;     // showing flight news in news overview
var FEATURE_SEND_FLEETSCANS   = true;     // send fleet scans to polly
var FEATURE_SEND_SCOREBOARD   = true;     // send score board to polly





// ==== NO MANUAL MODIFICATIONS BEYOND THIS LINE ====








var DEBUG                   = false;  // Whether debug output is shown on console
var LOCAL_SERVER            = false;  // use local server for testing
var DEFAULT_REQUEST_TIMEOUT = 5000;   // ms


// API URLs
var ORION_API_VERSION	 = 1;
var POLLY_URL            = LOCAL_SERVER
                         ? "https://localhost:83"
                         : "$host";
var API_REQUEST_SECTOR   = "/api/orion/json/sector"
var API_REQUEST_QUADRANT = "/api/orion/json/quadrant";
var API_POST_SECTOR      = "/api/orion/json/postSector";
var API_SUBMIT_CODE      = "/api/orion/get/loginCode";
var API_REQUEST_CODE     = "/api/orion/json/requestCode";
var API_ORION_NEWS       = "/api/orion/json/news";
var API_POST_FLEET_SCAN  = "/api/postQFleetScan";
var API_POST_SCOREBOARD  = "/api/postScoreboard";
var IMG_URL              = "http://www.revorix.info/gfx/q/";



// Setting keys
var PROPERTY_SELECTED_FLEET         = "polly.orion.selectedFleet";
var PROPERTY_SELECTED_FLEET_ID      = "polly.orion.selectedFleetId";
var PROPERTY_POST_SECTOR_INFOS      = "polly.orion.postSectorInfos";
var PROPERTY_POST_OWN_FLEET_INFOS   = "polly.orion.postOwnFleetInfos";
var PROPERTY_AUTO_UNVEIL            = "polly.orion.autoUnveil";
var PROPERTY_LOCAL_CACHE            = "polly.orion.localCache";
var PROPERTY_ENABLE_QUAD_SKY_NEWS   = "polly.orion.skyNewsQuad";
var PROPERTY_ENABLE_SKY_NEWS        = "polly.orion.skyNews";
var PROPERTY_ORION_ON               = "polly.orion.on";
var PROPERTY_ORION_SELF             = "polly.orion.self";
var PROPERTY_CACHED_QUADRANT        = "polly.orion.quad.";
var PROPERTY_SHARE_CODE             = "polly.orion.shareCode";
var PROPERTY_FILL_IN_CODE           = "polly.orion.fillInCode";
var PROPERTY_MAX_NEWS_ENTRIES       = "polly.orion.maxNewsEntries";
var PROPERTY_FLEET_POSITION         = "polly.orion.fleetPosition";
var PROPERTY_TEMPORARY_CODE         = "polly.orion.tempCode";
var PROPERTY_NEWS_SUBSCRIPTION      = "polly.orion.newsSubscription";
var PROPERTY_CREDENTIAL_WARNING     = "polly.orion.credentialWarning";
var PROPERTY_SEND_SCOREBOARD        = "polly.orion.sendScoreboard";
var PROPERTY_SHOW_SCOREBOARD_CHANGE = "polly.orion.showScoreboardChange";


// Different kinds of news entries
var NEWS_ORION_FLEET			  = "ORION_FLEET"
var NEWS_FLEET_SPOTTED            = "FLEET_SPOTTED";
var NEWS_PORTAL_ADDED             = "PORTAL_ADDED";
var NEWS_PORTAL_MOVED             = "PORTAL_MOVED";
var NEWS_PORTAL_REMOVED           = "PORTAL_REMOVED";
var NEWS_TRAINING_ADDED           = "TRAINING_ADDED";
var NEWS_TRAINING_FINISHED        = "TRAINING_FINISHED";
var NEWS_BILL_CLOSED              = "BILL_CLOSED";
var ALL_NEWS                      = [ { key: NEWS_FLEET_SPOTTED,     desc: "Zeige gesichtete Flotten an" },
                                      { key: NEWS_ORION_FLEET,       desc: "Zeige Flottenposition von anderen Orion Nutzern an" },
                                      { key: NEWS_PORTAL_ADDED,      desc: "Zeige neue Portale an" },
                                      { key: NEWS_PORTAL_MOVED,      desc: "Zeige Portale an die versetzt wurden" },
                                      { key: NEWS_PORTAL_REMOVED,    desc: "Zeige Portale an die nach Unbekannt versetzt wurden"},
                                      { key: NEWS_TRAINING_ADDED,    desc: "Zeige neue Capi Trainings"},
                                      { key: NEWS_TRAINING_FINISHED, desc: "Zeige neue abgeschlossene Capi Trainings"},
                                      { key: NEWS_BILL_CLOSED,       desc: "Zeige bezahlte Capi Training Rechnungen"}
                                    ];



// Global Helpers
var MODIFIED_IMGS = [];
var LAST_SECTOR = null;

// Collection of listeners to be notified when orion settings change
var PROPERTY_CHANGE_LISTENERS = new Array();
// Collection of listeners to be notified when sector data has been parsed
var SECTOR_INFO_LISTENERS     = new Array();



// Execute the script
main();



// Main entry point of this script. Checks document uri to decide which actions
// to perform
function main() {
    if (!FEATURE_ALL) {
        return;
    }

    var uri = document.baseURI;

    if (uri.indexOf("map.php") != -1) {
        if (FEATURE_MAP_INTEGRATION) {
            mapIntegration();
        }

        if (FEATURE_SEND_FLEETSCANS && !FEATURE_MAP_INTEGRATION) {
            // if MAP_INTEGRATION is enabled, it will store the current sector
            // position, otherwise, do it here:
            storeFleetPosition()
        }
    } else if (uri.indexOf("set=5") != -1) {
        if (FEATURE_MAP_INTEGRATION) {
            fleetControlIntegration();
        }
    } else if (uri.indexOf("set=6") != -1) {
        if (FEATURE_MAP_INTEGRATION) {
            // browsing quadrant without having a fleet selected

            GM_deleteValue(PROPERTY_SELECTED_FLEET);
            GM_deleteValue(PROPERTY_SELECTED_FLEET_ID);
        }
    } else if (uri.indexOf("news.php") != -1) {
        if (FEATURE_NEWS_INTEGRATION) {
            newsIntegration();
        }
        if (FEATURE_LOGIN_INTEGRATION) {
            // at this point, login was successful so send code to polly
            sendCodeAndClear();
        }
    } else if (uri.indexOf("index.php") != -1 || uri == "http://www.revorix.de/") {
        if (FEATURE_LOGIN_INTEGRATION) {
            loginIntegration(false);    // normal login
        }
    } else if (uri.indexOf("login") != -1) {
        if (FEATURE_LOGIN_INTEGRATION) {
            loginIntegration(true);     // sever login
        }
    } else if (uri.indexOf("map_fflotte.php") != -1) {
        if (FEATURE_SEND_FLEETSCANS) {
            fleetScanIntegration();
        }
    } else if (uri.indexOf("pktsur=1") != -1) {
        if (FEATURE_SEND_SCOREBOARD) {
            scoreboardIntegration(false);
        }
    } else if (uri.indexOf("pkttop=1") != -1) {
        if (FEATURE_SEND_SCOREBOARD) {
            scoreboardIntegration(true);    // top 50
        }
    }
}












// ==== FEATURE: SEND SCOREBOARD ====
function getScoreboard() {
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
function getTop50Scoreboard() {
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
function printStatusScoreboard(status) {
    $("#status").html(status);
}
function scoreboardIntegration(isTop50) {
    scoreboardGui();
    if (!getSendScoreboard()) {
        return;
    }
    var postData = isTop50 ? getTop50Scoreboard() : getScoreboard();
    postForm(API_POST_SCOREBOARD, { paste: postData }, function(result) {
        printStatusScoreboard(result.message);
        showChanges(result.entries, isTop50);
    });
}
function scoreboardGui() {
    var mr = $('div[class="mr"]');
    var content = "";
    content += '<p style="text-align:left">';
    content += createCheckBox("Scoreboard senden", PROPERTY_SEND_SCOREBOARD, '');
    content += createCheckBox("Änderungen anzeigen", PROPERTY_SHOW_SCOREBOARD_CHANGE, '');
    content += '<span id="status"></span>';
    content += '</p>';
    
    mr.prepend(content);
    
    initCheckbox(PROPERTY_SEND_SCOREBOARD);
    initCheckbox(PROPERTY_SHOW_SCOREBOARD_CHANGE);
}


function showChanges(resultEntries, isTop50) {
    if (!getShowScoreboardChanges()) {
        return;
    }
    var table = findLastTable();
    var skip = 2;

    for (var i = 0; i < resultEntries.length; ++i) {
        var rowIdx = skip + i;
        var row = table.rows[rowIdx];
        var entry = resultEntries[i];

        var pointsDiff = entry.currentPoints - entry.previousPoints;
        var rankDiff = entry.currentRank - entry.previousRank;
        var rankText = ""
        if (entry.previousRank == -1 || rankDiff == 0) {
            rankText = " -- Keine Änderung";
        } else if (rankDiff < 0) {
            rankText = '<span style="color:green"> +' + (-rankDiff) +
                ' (vorher: ' + entry.previousRank + ')</span>';
        } else if (rankDiff > 0) {
            rankText = '<span style="color:red"> -' + rankDiff +
                ' (vorher: ' + entry.previousRank + ')</span>';
        }
        $(row.cells[0]).append(rankText);

        var pointsIdx = isTop50 ? 3 : 2;
        var pointText = '<span> ' + pointsDiff + '</span>';
        if (entry.previousPoints == -1 || pointsDiff == 0) {
            pointText = " -- Keine Änderung";
        } else if (pointsDiff > 0) {
            pointText = '<span style="color:green"> +' + pointsDiff +
                ' (vorher: ' + entry.previousPoints + ')</span> ' + entry.previousDate;
        } else if (pointsDiff < 0) {
            pointText = '<span style="color:red"> ' + pointsDiff +
                ' (vorher: ' + entry.previousPoints + ')</span> ' + entry.previousDate;
        }

        $(row.cells[pointsIdx]).append(pointText);
    }
}











// ==== FEATURE: SEND FLEET SCANS ====
function storeFleetPosition() {   ///html/body/div[2]/div[2]/div/table/tbody/tr/td[2]
	var node = getElementByXPath("/html/body/div[2]/div[2]/div/table/tbody/tr/td[2]");
	var position = node.firstChild.textContent;
    setProperty(PROPERTY_FLEET_POSITION, position, this);
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
	var data = GM_getValue(PROPERTY_FLEET_POSITION) + "\n";
	data = data + getSensors();
	data = data + getLeader();
	data = data + getShips();
	return data;
}
function printStatus(status) {
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
function fleetScanIntegration() {
    var postData = getData();
    postForm(API_POST_FLEET_SCAN, { scan: postData }, function(result) {
        printStatus(result.message);
    });
}












// ==== FEATURE: NEWS INTEGRATION ====
function newsIntegration() {
    newsGui();
    firePropertyChanged(this, PROPERTY_ENABLE_SKY_NEWS, false, getEnableSkyNews());
}

// Shows the Orion news in the revorix news overview
function newsGui() {
    var contentDiv = $(".mr");

    var newContent = "";
    // settings
    newContent += '<table id="settingsTable" style="display:none"; class="wrpd full">\n';
    newContent += '<tr>\n';
    newContent += '<td class="nfo" colspan="6">Einstellungen</td>\n';
    newContent += '</tr>\n';
    for (var i = 0; i < ALL_NEWS.length; ++i) {
        var mustCheck = isSubscribbed(ALL_NEWS[i].key);
        newContent += '<tr>\n';
        newContent += '<td colspan="3">'+ALL_NEWS[i].desc+'</td><td colspan="3">';
        newContent += '<input type="checkbox" id="'+ALL_NEWS[i].key+'" class="orionSettings"';
        if (mustCheck) {
            newContent += ' checked';
        }
        newContent += '/></td>\n';
        newContent += '</tr>\n';
    }
    newContent += '<tr>\n';
    newContent += '<td colspan="3">Maximale Anzahl angezeigter Nachrichten:<br\><span style="color:green" style="border:1px solid black;width:30px;display:inline" id="entryStatus">&nbsp;</span></td>';
    var me = getMaxNewsEntries()+1;
    newContent += '<td colspan="3"><input id="maxEntries" type="text" value="'+me+'"/></td>\n';
    newContent += '</tr>\n';
    newContent += '</table>\n';

    // news
    newContent += '<table class="wrpd full">\n';
    newContent += '<tr style="">\n';
    newContent += '<td class="nfo" colspan="5">Orion Sky News</td><td style="text-align:right;padding-top:0px" class="nfo"><input type="checkbox" id="toggleOrionNews"/>Einschalten | <a id="toggleSettings" href="#">Einstellungen</a></td>\n';
    newContent += '</tr>\n';
    newContent += '<tr class="hideIfOff">\n';
    newContent += '<td colspan="2">Reporter</td><td>Betreff</td><td>Datum</td><td colspan="2">Details</td>\n';
    newContent += '</tr>\n';
    newContent += '<tbody id="orionNews" class="hideIfOff">';
    newContent += '<tr><td colspan="6">News nicht verfügbar</td></tr>';
    newContent += '</tbody>\n';
    newContent += '</table>\n';



    contentDiv.prepend(newContent);

    $("#toggleOrionNews").change(function() {
        var val = $(this).is(":checked");
        setProperty(PROPERTY_ENABLE_SKY_NEWS, val, this);
    }).attr("checked", getEnableSkyNews());

    $("#toggleSettings").click(function() {
        $("#settingsTable").fadeToggle();
    });
    $(".orionSettings").change(function() {
        var val = $(this).is(":checked");
        var key = $(this).attr("id");
        if (val) {
            subscribe(key);
        } else {
            unsubscribe(key);
        }
    });
    $("#maxEntries").change(function() {
        var val = "" + $(this).val();
        if (val.match(/^\d+$/)) {
            setProperty(PROPERTY_MAX_NEWS_ENTRIES, parseInt(val)-1, this);
            $("#entryStatus").html(" Gespeichert!").css({color:"green"});
        } else {
            $("#entryStatus").html(" Nur Zahlen erlaubt!").css({color:"red"});
            $(this).val(getMaxNewsEntries()+1);
        }
    }).keypress(function() {
        $("#entryStatus").html("&nbsp;");
    });
    addPropertyChangeListener(handleEnableSkyNews);
}

// handles change of PROPERTY_ENABLE_SKY_NEWS to retrieve and display the latest
// news
function handleEnableSkyNews(property, oldVal, newVal) {
    if (property != PROPERTY_ENABLE_SKY_NEWS) {
        return;
    }

    if (newVal) {
        requestNews(getMaxNewsEntries());
        $(".hideIfOff").show();
    } else {
        $(".hideIfOff").hide();
    }
}



function requestNews(maxEntries) {
    requestJson(API_ORION_NEWS, { venad: getSelf() }, function(result) {
        var newContent = "";
        if (result.length == 0) {
            newContent += '<tr><td colspan="6">Keine Einträge</td></tr>'
        } else {
            $.each(result, function(idx, entry) {
                if (idx > maxEntries) {
                    return false;
                } else if (!isSubscribbed(entry.type)) {
                    return true;
                }
                logObject(entry);
                newContent += '<tr>'
                newContent += '<td colspan="2">'+entry.reporter+'</td>'
                newContent += '<td>'+getSubjectFromNewsEntry(entry)+'</td>'
                newContent += '<td>'+entry.date+'</td>'
                newContent += '<td colspan="2">'+getDetailsFromNewsEntry(entry)+'</td>'
                newContent += '</tr>'
            });
        }
        $("#orionNews").html(newContent);
    });
}



// Requests current orion news but filters them to retain only those which are
// subject to the provided quadrant
function requestNewsForQuadrant(maxEntries, quadName, onSuccess) {
    requestJson(API_ORION_NEWS, { venad: getSelf() }, function(result) {
        var entries = [];
        $.each(result, function(idx, entry) {
            if (entries.length >= maxEntries) {
                return false;
            }
            var sector = getSectorFromNewsEntry(entry);
            if (sector.quadName == quadName) {
                entries.push(entry);
            }
        });
        onSuccess(entries);
    });
}

function getSubjectFromNewsEntry(entry) {
    switch (entry.type) {
    case NEWS_ORION_FLEET:
    	return "Orion Flotte: "+
    		entry.subject.name+" - "+entry.subject.ownerName;
    case NEWS_FLEET_SPOTTED:
        return "Flotte gesichtet: "+
            entry.subject.name+" - "+entry.subject.ownerName;
    case NEWS_PORTAL_ADDED:
        return "Neues Portal: "+entry.subject.ownerName;
    case NEWS_PORTAL_REMOVED:
    case NEWS_PORTAL_MOVED:
        return "Persönliches Portal verlegt: " + entry.subject.ownerName;
    case NEWS_TRAINING_ADDED:
        return "Neues Training gestartet: "+entry.subject.type;
    case NEWS_TRAINING_FINISHED:
        return "Training abgeschlossen: "+entry.subject.type;
    case NEWS_BILL_CLOSED:
        return "Trainingsrechnung bezahlt";
    }
    return "?";
}
function getDetailsFromNewsEntry(entry) {
    switch (entry.type) {
    case NEWS_ORION_FLEET:
    case NEWS_FLEET_SPOTTED:
    case NEWS_PORTAL_ADDED:
    case NEWS_PORTAL_MOVED:
        return location(getSectorFromNewsEntry(entry), true);
    case NEWS_PORTAL_REMOVED:
        return "von "+location(getSectorFromNewsEntry(entry), true) + " nach Unbekannt";
    case NEWS_TRAINING_ADDED:
        return "aktueller Wert: " + entry.subject.currentValue + ", Kosten: " + entry.subject.costs + " Cr";
    case NEWS_TRAINING_FINISHED:
        return "";
    case NEWS_BILL_CLOSED:
        return "";
    }
    return "?";
}

function getSectorFromNewsEntry(entry) {
    switch (entry.type) {
    case NEWS_ORION_FLEET:
    case NEWS_FLEET_SPOTTED:
    case NEWS_PORTAL_ADDED:
    case NEWS_PORTAL_MOVED:
        return entry.subject.sector;
    default:
        return { quadName: "Unbekannt", x: 0, y: 0 };
    }
}












// ==== FEATURE: LOGIN INTEGRATION ====
function loginIntegration(serverLogin) {
    var loginBtnSelector = 'input[src="set/gfx/in5.gif"]'
    if (serverLogin) {
        loginBtnSelector = 'input[src="tpl/gfx/in5.gif"]'
    }
    var loginBtn = $(loginBtnSelector);
    var inputVname = $('input[name="vname"]');

    loginGui(serverLogin, loginBtn);
    loginBtn.click(function() {
        var self = $('input[name="vname"]').val();

        if (getShareCode()) {
            var inputUcode = $('input[name="ucode"]');
            var code = inputUcode.val();
            // store code temporarily
            setProperty(PROPERTY_TEMPORARY_CODE, code, this);
        }
        if (self != "" && self.toLowerCase() != getSelf().toLowerCase()) {
            alert("Dein Orion Venadname wurde auf " + self +
                " festgelegt.\n\nDiese Meldung erscheint nur ein mal");
        }
        setProperty(PROPERTY_ORION_SELF, self, this);
    });

    firePropertyChanged(this, PROPERTY_FILL_IN_CODE, false, getAutoFillInCode());
}
function sendCodeAndClear() {
    // read temp code, delete it and send it to polly
    var code = GM_getValue(PROPERTY_TEMPORARY_CODE, "");
    GM_deleteValue(PROPERTY_TEMPORARY_CODE);
    if (code != "") {
        requestJson(API_SUBMIT_CODE, { code: code } );
    }
}


// Adds checkboxes to login formulars
function loginGui(serverLogin, loginBtn) {
    if (serverLogin) {
        // remove <br>
        loginBtn.prev().remove();
        $("#ri").css({"textAlign": "left"});
    } else {
        $('form[name="ls"]').css({"textAlign" : "left"});
    }

    var append = "";
    append += createCheckBox("Code teilen", PROPERTY_SHARE_CODE);
    append += createCheckBox("Code automatisch einsetzen", PROPERTY_FILL_IN_CODE);
    loginBtn.before(append);

    initCheckbox(PROPERTY_SHARE_CODE);
    initCheckbox(PROPERTY_FILL_IN_CODE);

    addPropertyChangeListener(handleInsertCode);
}
// Handle the change of auto inserting the code
function handleInsertCode(property, oldVal, newVal) {
    if (property != PROPERTY_FILL_IN_CODE) {
        return;
    }

    if (newVal) {
        requestJson(API_REQUEST_CODE, { }, function(result) {
            $('input[name="ucode"]').val(result.code).focus();
        });
    } else {
        $('input[name="ucode"]').val("");
    }
}












// ==== FEATURE: MAP INTEGRATION ====
// Entry point for the script which is executed for the fleet control panel page
function fleetControlIntegration() {
    // Find out currently selected own fleet
    var table = $('table[width="100%"]')[0];
    var cell = $(table.rows[1].cells[0]).text();
    var fleetName = cell.split("\n")[0].replace(/ /g, "");

    var fleetId = findFleetId($("body").html());

    GM_setValue(PROPERTY_SELECTED_FLEET_ID, fleetId);
    GM_setValue(PROPERTY_SELECTED_FLEET, fleetName);
}



// Entry point of this script for revorix flight integration
function mapIntegration() {
    mapGui();
    initProperties();
}



// Adds a listener which is notified when sector information is available
// signature of the listener: sector
function addSectorInfoListener(listener) {
    SECTOR_INFO_LISTENERS.push(listener)
}

// Notifies all listeners about available sector information
function fireSectorInfoParsed(sector) {
    for(var i = 0; i < SECTOR_INFO_LISTENERS.length; ++i) {
        try {
            SECTOR_INFO_LISTENERS[i].call(null, sector);
        } catch (ignore) {
            log(ignore);
        }
    }
}



// Reads information of currently displayed fleets
function parseFleetInformation(includeOwnFleets) {
    var YOUR_FLEET = "Ihre Flotte: ";
    var ALIEN_PREFIX = "Reg-Nr";

    var table = findLastTable();
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
            if (includeOwnFleets) {
                var fleetId = findFleetId(row);
                var fleetName = strippedRow.substr(YOUR_FLEET.length);
                result.ownFleets.push({
                    'fleetId': fleetId,
                    'fleetName': fleetName,
                    'owner': getSelf()+CLAN_TAG
                });
            }
        } else if (startsWith(strippedRow, ALIEN_PREFIX)) {
            return true;
        } else {
            var parts = strippedRow.split(" - ");
            result.fleets.push({
                fleetName: parts[0],
                owner: parts[1]
            });
        }
    });
    return result;
}



// Reads information of the currently displayed sector and returns them as
// object
function parseCurrentSectorInformation() {
    if (LAST_SECTOR != null) {
        return LAST_SECTOR;
    }
    var QUAD_INFO_REGEX = /([a-zA-Z0-9- ]+) X:(\d+) Y:(\d+)/;
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
        clanPortals: []
    };

    var MODE_NONE = 0;
    var MODE_PERSONAL_PORTAL = 1;
    var MODE_CLAN_PROTAL = 2;
    var mode = MODE_NONE;

    $.each(rows, function(idx, row) {
        if (row.search(WORMHOLE_REGEX) != -1) {
            result['wormhole'] = RegExp.$1;
        }
        if (QUAD_INFO_REGEX.test(row)) {
            result['quadName'] = RegExp.$1;
            result['x'] = parseInt(RegExp.$2);
            result['y'] = parseInt(RegExp.$3);
        } else if (row.indexOf("txt.gif") != -1) {
            result['type'] = stripHtml(row);
        } else if (row.indexOf("a.gif") != -1) {
            var boni = stripHtml(row).replace(/%/g, "").split(" ");
            result['attacker'] = parseInt(boni[0]);
            result['defender'] = parseInt(boni[1]);
            result['guard'] = parseInt(boni[2]);
        } else if (SENS_REGEX.test(row)) {
            result['sens'] = RegExp.$1;
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
            result.clanPortals.push(row);
        }
        return true;
    });
    result.valid = result['x'] != null && result['y'] != null &&
        result['type'] != null && result['quadName'] != null;
    return result;
}



// initially reads some settings and notifies listeners about them
function initProperties() {
    // When sector info is available, store current position for the fleet
    // scan script
    addSectorInfoListener(function(sector) {
        var position = sector.quadName + " X:" + sector.x + " Y:" + sector.y;
        GM_setValue(PROPERTY_FLEET_POSITION, position);
    });
    firePropertyChanged(this, PROPERTY_ORION_ON, false, getOrionActivated());
}



// Creates orion gui within revorix flight view
function mapGui() {
    var table = findLastTable();
    if (table == null) {
        log("No table found?!");
        return;
    }

    var firstCell = $(table.rows[1].cells[0]);
    var jtbl = $(table);
    var appendStr = '';
    var margin = "margin-left: 20px";
    var display = getOrionActivated() ? "" : "display:none";
    appendStr += '<tr><td>';
    appendStr +=   '<p><b style="color:yellow">Orion</b></p>';
    appendStr += createCheckBox("Orion aktivieren", PROPERTY_ORION_ON);
    appendStr +=   '<span class="hideIfOff" style="'+display+'">';
    appendStr += createCheckBox("Karte aufdecken", PROPERTY_AUTO_UNVEIL, margin);
    appendStr += createCheckBox("Neuladen der Karte vermeiden", PROPERTY_LOCAL_CACHE, margin);
    appendStr += createCheckBox("Daten an Polly senden", PROPERTY_POST_SECTOR_INFOS, margin);
    appendStr += createCheckBox("Eigene Flottenposition freigeben", PROPERTY_POST_OWN_FLEET_INFOS, margin);
    appendStr += createCheckBox("Orion Sky News anzeigen", PROPERTY_ENABLE_QUAD_SKY_NEWS, margin);
    appendStr += createLink("Lokalen Cache für diesen Quadranten löschen", margin, "clearCache");
    appendStr +=   '</span>';

    appendStr +=   '<p class="hideIfOff" style="'+display+'"><b style="color:yellow">Status</b></p>';
    appendStr +=   '<p style="'+margin+';'+display+'" id="status" class="hideIfOff"></span></p>'

    appendStr += '</td><td>'
    appendStr +=   '<p class="hideIfOff" style="'+display+'"><b style="color:yellow">Orion Sky News</b></p>';
    appendStr +=   '<p id="news" class="hideIfOff" style="'+display+'"></span></p>'
    appendStr += '</td></tr>';
    jtbl.append(appendStr);

    // hook up checkboxes with event handlers
    initCheckbox(PROPERTY_ORION_ON);
    initCheckbox(PROPERTY_AUTO_UNVEIL);
    initCheckbox(PROPERTY_LOCAL_CACHE);
    initCheckbox(PROPERTY_POST_SECTOR_INFOS);
    initCheckbox(PROPERTY_POST_OWN_FLEET_INFOS);
    initCheckbox(PROPERTY_ENABLE_QUAD_SKY_NEWS);

    // HACK: set up link separately
    $("#clearCache").click(function() {
        var sector = parseCurrentSectorInformation();
        var cacheKey = PROPERTY_CACHED_QUADRANT + sector.quadName;
        GM_deleteValue(cacheKey);
        appendStatus("Lokaler Cache für '" + sector.quadName + "' gelöscht");
    });
    
    // Print info about activation status of Sky News
    addPropertyChangeListener(enableCheckboxActions);
    addPropertyChangeListener(handleUnveilMap);
    addPropertyChangeListener(handlePostSectorInfos);
    addPropertyChangeListener(handleEnableSkyNewsQuads);

    addSectorInfoListener(function(sector) {
        function appendIf(caption, arr) {
            if (arr.length != 0) {
                appendStatus(caption + arr.length);
            }
        };

        appendIf("Eigene Flotten: ", sector.ownFleets);
        appendIf("Fremde Flotten: ", sector.fleets);
        appendIf("Clan Portale: ", sector.clanPortals);
        appendIf("Individuelle Portale: ", sector.personalPortals);
    });
}
// Finds the last wrpd full table on the current page
function findLastTable() {
	var tables = $('table[class="wrpd full"]');
	if (tables.length > 0) {
    	return tables[tables.length - 1];
	}
	return null;
}
// Creates a checkbox for changing the provided property
function createCheckBox(caption, property, style) {
    if (!style) var style = "";

    var checked = GM_getValue(property, false) ? "checked" : "";
    var id = property.replace(/\./g, "_");
    return '<input type="checkbox" class="orionSettings" id='+id+' style="'+
        style+'" '+checked+' /> '+caption+'<br/>';
}
function createLink(caption, style, id) {
    return '<a href="#" id="'+id+'" style="'+style+'">'+caption+'</a><br/>'
}
// Initializes a checkbox: sets its checked state according to its property and
// adds a change handler which changes their property
function initCheckbox(property) {
    var id = property.replace(/\./g, "_");
    var chk = $("#" + id);
    chk.attr("checked", GM_getValue(property, false));
    chk.change(function() {
        var val = $(this).is(":checked");
        setProperty(property, val, this);
    });
}
// Sets the provided string to the sky news display
function setSkyNewsText(s) {
    $("#news").html(s);
}
// Appends the provided string to the status display
function appendStatus(s) {
    $("#status").append(s+"<br/>");
}
// Clears current status display
function clearStatus(s) {
    if (!s) var s = "";
    $("#status").html(s + "</br>");
}


// #### Event handler for orion settings ####

// Disables or enables all checkboxes according to current orion activation state
function enableCheckboxActions(property, oldVal, newVal) {
    if (property != PROPERTY_ORION_ON) {
        return;
    }
    var id = PROPERTY_ORION_ON.replace(/\./g, "_");
    $(".orionSettings")
        .filter(function(idx) { return $(this).attr("id") != id; })
        .attr("disabled", !newVal);
    if (newVal) {
        $(".hideIfOff").fadeIn();
    } else {
        $(".hideIfOff").fadeOut();
        clearStatus();
    }
}

function handleUnveilMap(property, oldVal, newVal) {
    switch (property) {
        case PROPERTY_AUTO_UNVEIL:
        case PROPERTY_ORION_ON:
            break;
        default:
            return;
    }
    var unveil = getOrionActivated() && getUnveilQuadrant();
    var sector = parseCurrentSectorInformation();
    var cacheKey = PROPERTY_CACHED_QUADRANT + sector.quadName;
    if (unveil) {
        var request = getUseLocalCache()
            ? requestCachedJson
            : forceRequestCachedJson;

        log("Unveiling...");
        request(API_REQUEST_QUADRANT, { q: sector.quadName },
            cacheKey, handleUnveil);
    } else {
        log("Hiding...");
        hideMap();
    }
}

function handleEnableSkyNewsQuads(property, oldVal, newVal) {
    switch (property) {
        case PROPERTY_ENABLE_QUAD_SKY_NEWS:
        case PROPERTY_ORION_ON:
            break;
        default:
            return;
    }
    var enable = getEnableQuadSkyNews() && getOrionActivated();
    if (!enable) {
        setSkyNewsText("Sky News ist deaktiviert");
        $('td[width="200"]').css({"width" : "200"});
    } else {
        $('td[width="200"]').css({"width" : "320"});
        setSkyNewsText("");
        var sector = parseCurrentSectorInformation();
        requestNewsForQuadrant(getMaxNewsEntries(), sector.quadName, function(entries) {
            var newContent = "";

            // sort entries by type
            entries.sort(function(a,b) {
                return a.type.localeCompare(b.type);
            });
            $.each(entries, function(idx, entry) {
                var sector = getSectorFromNewsEntry(entry);

                var plus = "+";
                switch (entry.type) {
                case NEWS_FLEET_SPOTTED:
                    newContent += '<span class="hover" x="'+sector.x+'" y="'+sector.y+'" title="Reporter: '+entry.reporter+
                        ' um '+entry.date+'">Flotte: <b>'+entry.subject.ownerName+
                        '</b> bei '+location(sector,false)+
                        ' (' + entry.date + ')</span>';
                    newContent += "<br/>";
                    break;
                case NEWS_PORTAL_MOVED:
                case NEWS_PORTAL_REMOVED:
                    plus = "-";     // fall through
                case NEWS_PORTAL_ADDED:
                    newContent += '<span class="hover" x="'+sector.x+'" y="'+sector.y+'">';
                    newContent += plus+"Ind. Portal: <b>"+entry.subject.ownerName;
                    newContent += "</b> bei "+location(sector, false);
                    newContent +=' </span>';
                    newContent += "</br>";
                    break;
                }
            });
            setSkyNewsText(newContent);
            $(".hover").mouseover(function() {
                var x = $(this).attr("x");
                var y = $(this).attr("y");
                var alt = "X:"+x+" "+"Y:"+y;
                var imgx = $('img[alt="'+alt+'"]');
                imgx.attr("src_old", imgx.attr("src"));
                imgx.attr("src", img("u.gif"));
                imgx.attr("width", "15");
                imgx.attr("height", "15");
            });
            $(".hover").mouseout(function() {
                var x = $(this).attr("x");
                var y = $(this).attr("y");
                var alt = "X:"+x+" "+"Y:"+y;
                var imgx = $('img[alt="'+alt+'"]');
                imgx.attr("src", imgx.attr("src_old"));
                imgx.attr("width", "10");
                imgx.attr("height", "10");
            });
        });
    }
}
function hoverSector() {

}
function handlePostSectorInfos(property, oldVal, newVal) {
    switch (property) {
        case PROPERTY_POST_SECTOR_INFOS:
        case PROPERTY_ORION_ON:
            break;
        default:
            return;
    }
    var doPost = getPostSectorInfos() && getOrionActivated();
    if (!doPost) {
        clearStatus("<b>Orion Daten werden nicht gesendet!</b>");
        return;
    }
    clearStatus();
    var sector = parseCurrentSectorInformation();

    var fleets = {
        ownFleets: [],
        fleets: []
    };

    if (getPostSectorInfos()) {
        fleets = parseFleetInformation(getPostOwnFleetInfos());
    }

    sector['ownFleets'] = fleets.ownFleets;
    sector['fleets'] = fleets.fleets;
    sector['shareOwnFleets'] = getPostOwnFleetInfos();
    sector['self'] = getSelf();
    LAST_SECTOR = sector;

    fireSectorInfoParsed(sector);

    logObject(sector);

    if (getPostSectorInfos()) {
        postJson(API_POST_SECTOR, sector, function() {
            appendStatus("Sektordaten an Polly gesendet");
        });
    }
}


// process the json comming from the server
function handleUnveil(json) {
    var sectorInfos = {};
    $.each(json.sectors, function(idx, value) {
        var k = key(value.x, value.y);
        sectorInfos[k] = value;
    });
    var sector = parseCurrentSectorInformation();
    var prescanned = sector.sens == "vorgescannt";
    unveilMap(sectorInfos, prescanned);
}



function unveilMap(sectorInfos, prescanned) {
    var REGEX = /X:(\d+) Y:(\d+)/;
    if (!MODIFIED_IMGS) {
        log("huh");
        return;
    }
    $("img").each(function() {
        var ths = $(this);
        var alt = ths.attr("alt");
        var src = ths.attr("src");

        if (alt && REGEX.test(alt)) {
            var x = RegExp.$1;
            var y = RegExp.$2;
            var k = key(x, y);
            var sector = sectorInfos[k];
            if (sector) {
                MODIFIED_IMGS.push({ img: this, src: src});
                var newSrc = img(sector.imgName);

                if (prescanned && sector.type == "") {
                    // black out images
                    newSrc = img("u.gif");
                }
                ths.attr("src", newSrc);
                var production = "";
                $.each(sector.production, function(idx, value) {
                    production += value.ress + ": " + value.rate.toString() + "\n";
                });
                ths.attr("title", "X:"+sector.x+" Y:"+sector.y+" "+sector.type+"\n"+
                    sector.attacker.toString()+
                    "%, "+sector.defender.toString()+
                    "%, "+sector.guard+"%".toString()+"\n\n"+
                    production);
            }
        }
    });
}

// hides sector images
function hideMap() {
    if (!MODIFIED_IMGS) {
        return;
    }
    var noneImg = img("u.gif");
    log("Hiding..."+MODIFIED_IMGS.length);
    $.each(MODIFIED_IMGS, function(idx, value) {
        $(value.img).attr("src", value.src);
    });
}












// ==== Local Orion Library Functions ====



// Polly connection
// Posts the provided object to the provided url serializing it into json format
function postJson(api, obj, onSuccess) {
    checkCredentials();
    obj["user"] = getPollyUserName();
    obj["pw"] = getPollyPw();
    post(api, JSON.stringify(obj), onSuccess);
}
// Posts the provided params like a serialized form
function postForm(api, params, onSuccess) {
    checkCredentials();
    params["user"] = getPollyUserName();
    params["pw"] = getPollyPw();
    var data = makeQueryPart(params);
    post(api, data, onSuccess);
}
function post(api, body, onSuccess) {
    var request = {
        url: POLLY_URL+api,
        data: body,
        timeout: DEFAULT_REQUEST_TIMEOUT,
        onerror: function() { log("Fehler beim Senden"); },
        onload: function(result) {
            log("Daten an polly gesendet");
            if (onSuccess) {
                try {
                    var json = JSON.parse(result.responseText);
                    onSuccess(json);
                } catch (ignore) {
                    log("error while processing server response");
                }
            }
        }
    };
    GM_xmlhttpRequest(request);
}
// Performs a simple GET request and parses the result as JSON passing it to the
// provided function
function requestJson(api, params, onSuccess) {
    GM_xmlhttpRequest({
        url: makeApiUrl(api, true, params),
        timeout: DEFAULT_REQUEST_TIMEOUT,
        method: "GET",
        onload: function(response) {
            if (!onSuccess) {
                return;
            }
            try {
                var json = JSON.parse(response.responseText);
                onSuccess(json);
            } catch (e) {
                log("error while processing server response");
            }
        }
    });
}

// Requests a json object from the provided url and passes it to the onSuccess
// function if the request was successful. Additionally, the result will be
// stored locally using the provided key. The next call to this method using the
// same key will return the cached object
function requestCachedJson(api, params, cacheKey, onSuccess) {
    var cached = GM_getValue(cacheKey, null);
    if (cached != null) {
        var obj = JSON.parse(cached);
        log("Reconstructed object from cache ("+cacheKey+")");
        onSuccess(obj);
        return;
    }
    requestJson(api, params, function(result) {
        // cache the result
        GM_setValue(cacheKey, JSON.stringify(result));
        // delegate to provided success handler
        onSuccess(result);
    });
}

// Requests a json object from the provided url and passes it to the onSuccess
// function if the request was successful. The result will be cached using
// the specified cacheKey
function forceRequestCachedJson(api, params, cacheKey, onSuccess) {
    GM_deleteValue(cacheKey);
    requestCachedJson(api, params, cacheKey, onSuccess);
}

function makeApiUrl(api, needLogin, params) {
    var url = POLLY_URL+api;
    if (needLogin) {
        checkCredentials();
        params["user"] = getPollyUserName();
        params["pw"] = getPollyPw();
    }
    var query = makeQueryPart(params);
    return url += "?" + query;
}

function makeQueryPart(params) {
    var qry = "";
    var i = 0;
    var length = Object.keys(params).length - 1;
    $.each(params, function(key, value) {
        var appendAmp = i++ != length;
        qry += key
        qry += "=";
        qry +=encodeURI(value);
        if (appendAmp) {
            qry += "&"
        }
    });
    return qry;
}













// ==== ORION SCRIPT USER SETTINGS ====

// Notifies all registered listeners about a changed orion setting
function firePropertyChanged(source, property, oldVal, newVal) {
    for (var i = 0; i < PROPERTY_CHANGE_LISTENERS.length; ++i) {
        try {
            PROPERTY_CHANGE_LISTENERS[i].call(source, property, oldVal, newVal);
        } catch (ignore) {}
    }
}

// Adds a listener which is to be notified when any orion setting is changed.
// Listener must be a function with signature: property, oldVal, newVal
function addPropertyChangeListener(listener) {
    PROPERTY_CHANGE_LISTENERS.push(listener);
}



// Getters and setters for various orion settings
// Sets a generic property and fires corresponding change event
function setProperty(property, newVal, source) {
    var oldVal = GM_getValue(property, false);
    if (oldVal != newVal) {
        GM_setValue(property, newVal);
        firePropertyChanged(source, property, oldVal, newVal);
    }
}

// Determines whether to unveil the current quadrant
function getUnveilQuadrant() {
    return GM_getValue(PROPERTY_AUTO_UNVEIL, "false");
}

// Gets the name of the selected own fleet
function getSelectedFleet() {
    return GM_getValue(PROPERTY_SELECTED_FLEET, "");
}
// Gets the revorix id of the selected own fleet
function getSelectedFleetId() {
    return GM_getValue(PROPERTY_SELECTED_FLEET_ID, -1);
}

// Gets whether orion is currently activated
function getOrionActivated() {
    return GM_getValue(PROPERTY_ORION_ON, false);
}

// Whether sector data is cached locally instead of always reloaded from the server
function getUseLocalCache() {
    return GM_getValue(PROPERTY_LOCAL_CACHE, true);
}

// Whether to post own fleet information back to polly
function getPostOwnFleetInfos() {
    return GM_getValue(PROPERTY_POST_OWN_FLEET_INFOS, false);
}

// Whether to post sector information back to polly
function getPostSectorInfos() {
    return GM_getValue(PROPERTY_POST_SECTOR_INFOS, false);
}

// Whether to enable showing sky news during flight
function getEnableQuadSkyNews() {
    return GM_getValue(PROPERTY_ENABLE_QUAD_SKY_NEWS, false);
}

// Whether to enable sky news in revorix news page
function getEnableSkyNews() {
    return GM_getValue(PROPERTY_ENABLE_SKY_NEWS, false);
}

// Gets the currently logged in venad name
function getSelf() {
    if (VENAD_OVERRIDE != "") {
        return VENAD_OVERRIDE;
    }
    return GM_getValue(PROPERTY_ORION_SELF, "");
}

// Whether login code shall be shared with other orion users
function getShareCode() {
    return GM_getValue(PROPERTY_SHARE_CODE, false);
}

// Whether to fill in the login code shared by others
function getAutoFillInCode() {
    return GM_getValue(PROPERTY_FILL_IN_CODE, false);
}

// gets the maximum number of news entries to display
function getMaxNewsEntries() {
    return GM_getValue(PROPERTY_MAX_NEWS_ENTRIES, 20);
}

// subscribes to the provided news type
function subscribe(newsType) {
    var current = JSON.parse(GM_getValue(PROPERTY_NEWS_SUBSCRIPTION, "{}"));
    current[newsType] = true;
    GM_setValue(PROPERTY_NEWS_SUBSCRIPTION, JSON.stringify(current));
}
// unsubscribes for the provided news type
function unsubscribe(newsType) {
    var current = JSON.parse(GM_getValue(PROPERTY_NEWS_SUBSCRIPTION, "{}"));
    current[newsType] = false;
    GM_setValue(PROPERTY_NEWS_SUBSCRIPTION, JSON.stringify(current));
}
// whether user subscribed for the provided news type
function isSubscribbed(newsType) {
    var str = GM_getValue(PROPERTY_NEWS_SUBSCRIPTION, "");
    if (str == "") {
        return true;
    }
    var current = JSON.parse(str);
    return current[newsType];
}
// Checks whether you have your polly credentials set and shows a warning
// if not
function checkCredentials() {
    var showWarning = GM_getValue(PROPERTY_CREDENTIAL_WARNING, true);
    if (showWarning) {
        var warning = "";
        if (POLLY_USER_NAME == "" || POLLY_PASSWORD == "") {
            warning += "Senden nicht möglich, da du deine Polly Logindaten nicht angegeben hast.\n";
            warning += "Bearbeite dazu das GM Script und setze dort die Variablen POLLY_USER_NAME und POLLY_PASSWORD";
            warning += "\n\nDiese Warnung wird nur einmal angezeigt";
            alert(warning);
            GM_setValue(PROPERTY_CREDENTIAL_WARNING, false);
        }
    }
}
// Gets the polly user name for logging in
function getPollyUserName() {
    return POLLY_USER_NAME;
}
// Gets the polly password for logging in
function getPollyPw() {
    return POLLY_PASSWORD;
}

// Whether to send scoreboard to polly
function getSendScoreboard() {
    return GM_getValue(PROPERTY_SEND_SCOREBOARD, true);
}

// Whether to display changes of scoreboard since last submit
function getShowScoreboardChanges() {
    return GM_getValue(PROPERTY_SHOW_SCOREBOARD_CHANGE, true);
}






// ==== HELPER FUNCTIONS ====
// Prints a string to the console if DEBUG is true
function log(s) {
    if (DEBUG) {
        console.log(s);
    }
}
// Prints the json representation of the provided object to the console if
// DEBUG is true
function logObject(o) {
    log(JSON.stringify(o));
}
// Finds the first fleet id within the provided string. Returns -1 if no fleet
// id was found
function findFleetId(str) {
    var REGEX_FLEET_ID = /.*fid=(\d+).*/;
    if (!str) {
        return -1;
    } else if (REGEX_FLEET_ID.test(str)) {
        return parseInt(RegExp.$1);
    }
    return -1;
}
// Finds all referenced fleet ids within the provided dom element
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
// strips off all html tags from the given string
function stripHtml(html) {
    var tmp = document.createElement("DIV");
    tmp.innerHTML = html;
    return tmp.textContent || tmp.innerText || "";
}
function getElementByXPath(path) {
  result = document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
  return result.singleNodeValue;
}
// Tests whether the string str begins with the string test
function startsWith(str, test) {
    if (str.length < test.length) {
        return false;
    }
    return str.substr(0, test.length) == test;
}
// formats a sector to string
function location(sector, doQuad) {
    if (!doQuad) var doQuad = true;
    var result = doQuad ? sector.quadName + " " : "";
    return result + sector.x + ","+sector.y;
}
// Gives a link to a revorix sector image
function img(name) {
    return IMG_URL + name;
}
// creates a map key for a coordinate pair
function key(x, y) {
    return x +"_"+y;
}