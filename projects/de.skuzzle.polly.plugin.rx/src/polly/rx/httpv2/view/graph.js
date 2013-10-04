function loadGraph(url, target, venad, maxMonths) {
    $.get(url + "?venadName="+venad+"&maxMonth="+maxMonths, function(data) {
        $("#" + target).html(data);
    });
}
function loadGraphCompare(url, target, maxMonths) {
	$.get(url + "?maxMonth="+maxMonths, function(data) {
        $("#" + target).html(data);
	});
}

function selectMM(url, hash, name) {
	var selectId = "selectMonths_" + hash;
	var select = $("#" + selectId);
	var maxMonths = $(select).find(":selected").val();
	loadGraph(url, hash, name, maxMonths);
}
function selectMMCompare(url, target) {
	var selectId = "selectMonths_cmp";
	var select = $("#" + selectId);
	var maxMonths = $(select).find(":selected").val();
	loadGraphCompare(url, target, maxMonths);
}