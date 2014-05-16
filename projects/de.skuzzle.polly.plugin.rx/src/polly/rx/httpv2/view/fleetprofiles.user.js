#parse ( "/polly/rx/httpv2/view/fleetprofiles.meta.js" )



/* 
Changelog
[ CURRENT ] Version 0.1 - 16.05.2014
	Features:
		+ GUI to add/modify and delete fleet profiles
		+ Automatically chose best matching fleet profile
		+ Manually chose different profile if needed
*/



// Possible types for the 'entry' field in a profile. DO NOT MODIFY THESE VALUES
var ENTRY_PORTAL     = 1;
var ENTRY_SECTOR     = 2;
var ENTRY_INDIVIDUAL = 3;
var ENTRY_CLAN       = 4;
var ENTRY_TYPES      = [
    { name : "Entrittsportale",      id : ENTRY_PORTAL },
    { name : "Sektoren",             id : ENTRY_SECTOR },
    { name : "Individuelles Portal", id : ENTRY_INDIVIDUAL },
    { name : "Clan Portal",          id : ENTRY_CLAN }
];


// Possible types for the 'quad' field in a profile when 'entry' is 
// set to ENTRY_PORTAL. DO NOT MODIFY THESE VALUES
var QUAD_JERICHO_21_9        = 50675;
var QUAD_JERICHO_15_11       = 50709;
var QUAD_JERICHO_6_14        = 50752;
var QUAD_JERICHO_33_13       = 53143;
var QUAD_LYRA_MAJOR_3_6      = 36631;
var QUAD_NEW_HOPE_20_6       = 45456;
var QUAD_NEW_HOPE_19_16      = 45618;
var QUAD_NEW_HOPE_19_28      = 45786;
var QUAD_OCULUM_CORVUS_20_24 = 39797;
var ALL_QUADS = [
    { name : "Jericho 21, 9",        id : QUAD_JERICHO_21_9 },
    { name : "Jericho 15, 1",        id : QUAD_JERICHO_15_11 },
    { name : "Jericho 6, 14",        id : QUAD_JERICHO_6_14 },
    { name : "Jericho 33, 13",       id : QUAD_JERICHO_33_13 },
    { name : "Lyra Major 3, 6",      id : QUAD_LYRA_MAJOR_3_6 },
    { name : "New Hope 20, 6",       id : QUAD_NEW_HOPE_20_6 },
    { name : "New Hope 19, 16",      id : QUAD_NEW_HOPE_19_16 },
    { name : "New Hope 19, 28",      id : QUAD_NEW_HOPE_19_28 },
    { name : "Oculum Corvus 20, 24", id : QUAD_OCULUM_CORVUS_20_24 }
];

//Strings
//from: http://stackoverflow.com/questions/610406/javascript-equivalent-to-printf-string-format
if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined' ? args[number] : match;
        });
    };
}


var MSG_NAME_NOT_VALID = "'{0}' ist kein gültiger Profilname. Erlaubte Zeichen: a-z, 0-9 und _";
var MSG_PROFILE_EXISTS = "Profil '{0}' existiert bereits";
var MSG_PROFILE_MANAGER = "Profil Manager";
var MSG_NEW_PROFILE = "Neues Profil";
var MSG_ADD = "Hinzufügen";
var MSG_ADD_SHORT = "+";
var MSG_REMOVE_SHORT = "-";
var MSG_LIST_PROFILES = "Profile:";
var MSG_PROFILE = "Profil";
var MSG_FLEET_NAME = "Flottenname:";
var MSG_FLEET_NAME_HINT = "(leer lassen um Revorix-generierten Namen zu verwenden)";
var MSG_FLEET_PW = "Passwort:";
var MSG_LEAVE_EMPTY_HINT = "(oder leer lassen)";
var MSG_FLEET_TAG = "Tag:";
var MSG_ENTRY_TYPE = "Sprungort:";
var MSG_ACTIVATE_TARN = "Sofort tarnen";
var MSG_IGNORE_PROFILE = "Profil deaktivieren";
var MSG_NO_PROFILES = "Keine Profile Eingerichtet";
var MSG_SAVE_PROFILE = "Speichern";
var MSG_REMOVE_PROFILE = "Profil löschen";
var MSG_SAVED = "Gespeichert";
var MSG_DELETED = "Profil '{0}' wurde gelöscht";
var MSG_ENABLE_PROFILES = "Profile editieren";
var MSG_ADD_TO = "Zum Profil hinzufügen";
var MSG_REMOVE_FROM = "Aus Profil entfernen";



var PROPERTY_PROFILES = "polly.portal.PROFILES";
var PROPERTY_ENABLE_PROFILES = "polly.portal.ENABLE";


var TOGGLE = {};
var RX_FLEET_NAME = "";
var ID_REGEX = /\?sid=(\d+)/;




// run the script
main();



function main() {
    var uri = document.baseURI;
    
    if (uri.indexOf("schiff_portal") != -1) {
        portalIntegration();
    } else if (uri.indexOf("schiff_list") != -1) {
        shipListIntegration();
    }
}



function shipListIntegration() {
    shipListGui();
}



function shipListGui() {
    var profiles = getProfiles();
    var tbl = findLastTable();
    var c = "";
    c += '<table class="wrpd full profile" style="margin-top:10px";>';
    c += '<thead>';
    c += '<tr><th colspan="2" class="nfo">{0}</th></tr>'.format(MSG_PROFILE_MANAGER);
    c += '</thead>';
    c += '<tbody>';
    c += '<tr><td>{0}</td>'.format(MSG_NEW_PROFILE);
    c += '<td><input type="text" name="nwprflnm"/> <input type="button" value="{0}" name="ddprfl"/></td>'.format(MSG_ADD);
    c += '</tr>';
    c += '<td></td>';
    c += '<td id="changeProfile">'+profileTable()+'</td>';
    c += '</tr>';
    c += '</tbody>';
    c += '</table>';
    $(tbl).after(c);
    
    $('input[name="ddprfl"]').click(handleAddProfile);
    $('input[name="svprlf"]').click(handleSaveProfile);
    $('input[name="rmprlf"]').click(handleRemoveProfile);
    adjustShipTable();
}

// handles click of Add Profile button
function handleAddProfile() {
    var name = $('input[name="nwprflnm"]').val();
    try {
        addProfile(name);
        $("#profilesTop").append('<option value="{0}">{0}</option>'.format(name));
        $("#profilesTop").trigger("change");
    } catch (e) {
        alert(e);
        return;
    }
}

function handleSaveProfile() {
    var name = $("#profilesTop").val();
    var profile = getProfiles()[name];
    profile["name"] = $('input[name="fltnm"]').val();
    profile["password"] = $('input[name="fltpw"]').val();
    profile["tag"] = $('input[name="flttg"]').val();
    profile["tarn"] = $('input[name="chktrn"]').is(":checked");
    profile["ignore"] = $('input[name="chkgnr"]').is(":checked");
    profile["entry"] = $('select[name="ntrytyp"]').val();
    profile["quad"] = $('select[name="qdtyp"]').val();

    storeProfiles(getProfiles());
    alert(MSG_SAVED);
}

function handleRemoveProfile() {
    var name = $("#profilesTop").val();
    deleteProfile(name);
    $("#profilesTop").find('option[value="'+name+'"]').remove();
    $("#profilesTop").trigger("change");
    alert(MSG_DELETED.format(name));
}

// returns the html content string for a <select> element containing all profiles
function profilesAsOptions(profiles) {
    var cnt = "";
    $.each(profiles, function(k, v) {
        cnt += '<option value="'+k+'">'+k+'</option>';
    });
    return cnt;
}

function adjustShipTable() {
    var table = findShipTable();
    var profiles = getProfiles();

    var bfr = "";
    bfr = '<div style="margin-bottom: 10px" class="wrpd fulL"><input type="checkbox" id="enable"/><label for="enable">{1}</label> <span class="profile" style="margin-left:15px">{0} </span><select class="profile" id="profilesTop"></select></div>'.format(MSG_PROFILE, MSG_ENABLE_PROFILES);
    table.before(bfr);
    table.attr("id", "shipTable");
    $("#enable").prop("checked", getEnableProfiles());
    $("#enable").change(function() {
        var enable =  $(this).is(":checked");
        GM_setValue(PROPERTY_ENABLE_PROFILES, enable);
        if (enable) {
            $(".profile").show();
            $("#profilesTop").trigger("change");
        } else {
            $(".profile").hide();
            resetColors();
        }
    });

    
    $("#profilesTop").html(profilesAsOptions(profiles));
    $("#profilesTop").change(function() {
        if (!getEnableProfiles()) {
            return;
        }
        var name = $(this).val();
        var profiles = getProfiles();

        if (name === null) {
            // no profiles exist
            $("#selProfile").html("{0}: {1}".format(MSG_PROFILE, "&lt;Kein&gt;"));
            $("#editProfile").hide();
            $(".addTo").hide();
            $(".removeFrom").hide();
        } else {
            $("#editProfile").show();
            var profile = getProfiles()[name];
            colorShips(profile);
            hideShowButtons(profile);
            $("#selProfile").html("{0}: {1}".format(MSG_PROFILE, name));
            showProfile(name, profile);
        }

    });
    
    table.find("tr:nth-child(1) td").attr("colspan", "11");
    table.find("tr:nth-child(2)").append("<td class='profile' id='selProfile'></td>");
    table.find("tr:nth-child(n+3)").each(function() {
        var name = $("#profilesTop").val();
        var profile = getProfiles()[name];
        var ftd = $(this).find("td:nth-child(1)").first();
        var id = idFromTd(ftd);
        $(this).append('<td class="profile" style="text-align:center"><a href="#" shipid="{1}" class="addTo" title="{3}">+++</a><a href="#" shipid="{1}" class="removeFrom" title="{4}">---</a></td>'.format(MSG_ADD_SHORT, id, MSG_REMOVE_SHORT, MSG_ADD_TO, MSG_REMOVE_FROM));
    });
    
    $(".addTo").click(function() {
        var name = $("#profilesTop").val();
        var profile = getProfiles()[name];
        var shipId = parseInt($(this).attr("shipid"), 10);
        addShip(profile, shipId);
        colorShips(profile);
        hideShowButtons(profile);
    });
    $(".removeFrom").click(function() {
        var name = $("#profilesTop").val();
        var profile = getProfiles()[name];
        var shipId = parseInt($(this).attr("shipid"), 10);
        
        removeShip(profile, shipId);
        colorShips(profile);
        hideShowButtons(profile);
    });
    
    $("#profilesTop").trigger("change");
    $("#enable").trigger("change");
}

// extracts a ship id from the provided td and returns it as string
function idFromTd(ftd) {
    var a = ftd.find("a").first();
    var href = a.attr("href");
    var match = ID_REGEX.exec(href);
    var id = match[1];
    return id;
}

function resetColors() {
    var table = findShipTable();
    table.find("tr:nth-child(n+3)").each(function() {
        $(this).css( { color : "white" } );
    });
}

function colorShips(profile) {
    var table = findShipTable();
    table.find("tr:nth-child(n+3)").each(function() {
        var ftd = $(this).find("td:nth-child(1)").first();
        var ids = idFromTd(ftd);
        var id = parseInt(ids, 10);
        if ($.inArray(id, profile.ids) == -1) {
            ftd.parent().css( { color : "#626d82" } );
        } else {
            ftd.parent().css( { color : "white" } );
        }
    });
}

function hideShowButtons(profile) {
    $(".addTo").each(function() {
        var id = parseInt($(this).attr("shipid"), 10);
        if ($.inArray(id, profile.ids) != -1) {
            $(this).hide();
        } else {
            $(this).show();
        }
    });
    $(".removeFrom").each(function() {
        var id = parseInt($(this).attr("shipid"), 10);
        if ($.inArray(id, profile.ids) == -1) {
            $(this).hide();
        } else {
            $(this).show();
        }
    });
}


function showProfile(name, profile) {
    var entryTypeSelect = $('select[name="ntrytyp"]');
    var quadTypeSelect = $('select[name="qdtyp"]');
    
    // fill select input with different entry types
    var opt = "";
    $.each(ENTRY_TYPES, function(idx) {
        var entry = ENTRY_TYPES[idx];
        opt += '<option value="'+entry.id+'">'+entry.name+'</option>';
    });
    entryTypeSelect.html(opt);
    entryTypeSelect.val(profile["entry"]);
    
    // change handler to show/hide the quad selector
    entryTypeSelect.change(function() {
        var val = parseInt($(this).val(), 10);
        if (val == ENTRY_PORTAL) {
            quadTypeSelect.show();
        } else {
            quadTypeSelect.hide();
        }
    });

    // fill select input with different entry portals
    var quadopt = "";
    $.each(ALL_QUADS, function(idx) {
        var quad = ALL_QUADS[idx];
        quadopt += '<option value="'+quad.id+'">'+quad.name+'</option>';
    });
    quadTypeSelect.html(quadopt);
    quadTypeSelect.val(profile["quad"]);
    
    $("#profileName").html("{0}: <b>{1}</b>".format(MSG_PROFILE, name));
    $('input[name="fltnm"]').val(profile["name"]);
    $('input[name="fltpw"]').val(profile["password"]);
    $('input[name="flttg"]').val(profile["tag"]);
    $('input[name="ntrytyp"]').val(profile["entry"]);
    $('input[name="qdtyp"]').val(profile["quad"]);
    $('input[name="chktrn"]').prop("checked", profile["tarn"]);
    $('input[name="chkgnr"]').prop("checked", profile["ignore"]);
    entryTypeSelect.trigger("change");
}


function profileTable() {
    var c = "";
    c += '<table id="editProfile" style="width:100%">';
    c += '<tr><td id="profileName" colspan="2">{0}</td></tr>';
    c += '<tr><td>{0}</td><td><input type="text" name="fltnm"/> {1}</td></tr>'.format(MSG_FLEET_NAME, MSG_FLEET_NAME_HINT);
    c += '<tr><td>{0}</td><td><input type="text" name="fltpw"/> {1}</td></tr>'.format(MSG_FLEET_PW, MSG_LEAVE_EMPTY_HINT);
    c += '<tr><td>{0}</td><td><input type="text" name="flttg"/> {1}</td></tr>'.format(MSG_FLEET_TAG, MSG_LEAVE_EMPTY_HINT);
    c += '<tr><td>{0}</td><td><select name="ntrytyp"></select> <select name="qdtyp"></select></td></tr>'.format(MSG_ENTRY_TYPE);
    c += '<tr><td></td><td><input type="checkbox" name="chktrn" id="chktrn"/> <label for="chktrn">{0}</label></td></tr>'.format(MSG_ACTIVATE_TARN);
    c += '<tr><td></td><td><input type="checkbox" name="chkgnr" id="chkgnr"/> <label for="chkgnr">{0}</label></td></tr>'.format(MSG_IGNORE_PROFILE);
    c += '<tr><td></td><td><input type="button" name="svprlf" value="{0}"/> <input type="button" name="rmprlf" value="{1}"/></td></tr>'.format(MSG_SAVE_PROFILE, MSG_REMOVE_PROFILE);
    c += '</table>';
    return c;
}

function removeShip(profile, id) {
    var idx = $.inArray(id, profile.ids);
    if (idx == -1) {
        return;
    }
    profile.ids.splice(idx, 1);
}
function addShip(profile, id) {
    if ($.inArray(id, profile.ids) != -1) {
        return;
    }
    profile.ids.push(id);
    storeProfiles(getProfiles());
}

function addProfile(name) {
    if (!isValidProfileName(name)) {
        throw MSG_NAME_NOT_VALID.format(name);
    }
    var profiles = getProfiles();
    if (profiles[name] != undefined) {
        throw MSG_PROFILE_EXISTS.format(name);
    }
    profiles[name] = {
        ids      : [],
        name     : "",
        password : "",
        tag      : "",
        entry    : ENTRY_PORTAL,
        quad     : QUAD_NEW_HOPE_19_28,
        tarn     : true,
        ignore   : false
    };
    storeProfiles(profiles);
}

// deletes a whole profile
function deleteProfile(name) {
    var profiles = getProfiles();
    var deleted = profiles[name];
    delete profiles[name];
    storeProfiles(profiles);
}

function isValidProfileName(name) {
    return /[a-zA-Z0-9_]+/.test(name);
}


// store profiles to settings
function storeProfiles(profiles) {
    var str = JSON.stringify(profiles);
    GM_setValue(PROPERTY_PROFILES, str);
}
// Read profiles from settings
var profileStore;
function getProfiles() {
    if (profileStore == undefined) {
        profileStore = readProfiles();
    }
    return profileStore;
}
function readProfiles() {
    var str = GM_getValue(PROPERTY_PROFILES, "{}");
    return JSON.parse(str);
}



function portalIntegration() {
    // save rx generated fleet name
    RX_FLEET_NAME = $('input[name="fname"]').val();

    var bm = findBestMatchingProfile();

    if (enableAutoSelectProfile()) {
        selectProfile(bm.best, true);
        TOGGLE[bm.name] = false;
    }

    portalGui(bm);
}


function portalGui(bestMatch) {
    $('table[class="full gnfo"] tr:nth-child(1)').append('<td class="gnfo"></td>');
    $('table[class="full gnfo"] tr:nth-child(2)').append('<td id="prfls"></td>');
    $('table[class="full gnfo"] tr:nth-child(3)').append('<td class="gnfo"></td>');
    var prf = "";

    $.each(getProfiles(), function (k, v) {
        if (v.ignore) { return true; }
        var matches = bestMatch.matches[k];
        var ratio = roundn(matches / v.ids.length, 2);
        if (matches !== 0) {
            prf += '<a class="prfllnk" href="#" name="'+k+'">';
        }
        prf += k;
        if (matches !== null) {
            prf +='</a>';
        }
        prf += ' (' + matches + "/"+ v.ids.length +", "+ (ratio*100.0)+ '%)';
        if (v == bestMatch.best) {
            prf += " &lt;-";
        }
        prf += "</br>";
    });

    $("#prfls").html(prf);
    $('.prfllnk').click(profileClick);
}

// loads a profile if its link was clicked
function profileClick() {
    var name = $(this).attr("name");
    var check = TOGGLE[name] == undefined ? true : TOGGLE[name];
    $.each(getProfiles(), function (k, v) {
        if (v.ignore) { return true; }
        TOGGLE[k] = true;
    });
    TOGGLE[name] = !check;
    selectProfileByName(name, check);
}



function findBestMatchingProfile() {
    var ships = $('input[name="fships[]"]');
    var matches = {};
    var ret = {
        best : null,
        name : null,
        matches : {}
    };
    // Count occurrence of ships
    ships.each(function() {
        var id = $(this).val();
        $.each(getProfiles(), function (k, v) {
            if (v.ignore) { return true; }

            if (ret.matches[k] == undefined) { 
                ret.matches[k] = 0;
            }
            $.each(v.ids, function (idx) {
                if (v.ids[idx] == id) {
                    ret.matches[k] += 1;
                }
            });
        });
    });


    // select profile with best matches to ship count ratio
    $.each(getProfiles(), function (k, v) {
        if (v.ignore) { return true; }
        
        if (ret.best === null) { 
            ret.best = v; 
            ret.name = k;
        }
        
        var cRatio = ret.matches[k] / v.ids.length;
        var bRatio = ret.matches[ret.name] / ret.best.ids.length;
        if (cRatio >= bRatio) {
            ret.best = v;
            ret.name = k;
        }
    });

    return ret;
}
// select ships from the provided profile
function selectProfile(profile, check) {
    // select ships in this profile
    var ships = $('input[name="fships[]"]');
    ships.each(function() {
        var id = parseInt($(this).val(), 10);
        this.checked = check && $.inArray(id, profile.ids) != -1;
    });
    
    // trigger recalculation of admiralität
    ships.first().trigger("click");
    ships.first().trigger("click");
    
    // set fleet name if specified in profile
    if (check && profile["name"] != undefined && profile["name"] != "") {
        $('input[name="fname"]').val(profile["name"]);
    } else {
        // set name to the rx generated one
        $('input[name="fname"]').val(RX_FLEET_NAME);
    }
    
    // set fleet tag if specified
    if (check && profile["tag"] != undefined) {
        $('input[name="ftg"]').val(profile["tag"]);
    } else {
        // reset tag if none was specified
        $('input[name="ftg"]').val("");
    }
    
    // set fleet pw if specified
    if (check && profile["password"] != undefined) {
        $('input[name="anschluss"]').val(profile["password"]);
    } else {
        $('input[name="anschluss"]').val("");
    }
    
    // set entry point
    if (profile["entry"] != undefined) {
        var e = profile["entry"];
        $('input[name="sptr"][value="'+e+'"]').prop("checked", true);
    
        var sl = $('select[name="fport"]');
        sl.prop("disabled", e != ENTRY_PORTAL);
        if (e == ENTRY_PORTAL) {
            var quad = profile["quad"];
            if (quad == undefined) {
                alert("Quadrant wurde nicht angegeben");
            } else {
                sl.val(quad);
            }
        }
    }
    
    // set tarn
    if (check && profile["tarn"] != undefined) {
        $('input[name="flstl"]').prop("checked", profile["tarn"]);
    } else {
        // default is on
        $('input[name="flstl"]').prop("checked", true);
    }
}
// Whether profile manager is activated in ship overview
function getEnableProfiles() {
    return GM_getValue(PROPERTY_ENABLE_PROFILES, true);
}
// select ships from a profile by name
function selectProfileByName(name, check) {
    var profile = getProfiles()[name];
    selectProfile(profile, check);
}
// whether to initially select best matching profile
function enableAutoSelectProfile() {
    return true;
}
// rounds a number to the specified amount of digits
function roundn(num, dig) {
    var p = Math.pow(10, dig);
    return Math.round(num * p) / p;
}

//Finds the last wrpd full table on the current page
function findLastTable() {
    var tables = $('table[class="wrpd full"]');
    if (tables.length > 0) {
        return tables[tables.length - 1];
    }
    return null;
}
// finds the table which displays the ships
function findShipTable() {
    var tables = $('table[class="wrpd full"]');
    var result = tables.first();
    tables.each(function() {
        var td = $(this).find('.nfo');
        if (td.length != 0) {
            result = $(this);
            return false;
        }
        return true;
    });
    return result;
}