function makeUrl(baseUrl, parameters){
	var result = baseUrl;
	var qryPart = "";
	var append = "&";
	if (baseUrl.search(/\?/) == -1) {
		append = "?";
	}
	$.each(parameters, function(key, value) {
		qryPart += append;
		qryPart += encodeURIComponent(key) + "=" + encodeURIComponent(value);
		append ="&";
	});
	return baseUrl + qryPart;
}
function sortTableRequest(url, id, col) {
	var getUrl = makeUrl(url, {sort: col});
    loadTable(getUrl, id);
}
function reloadAll(url, id) {
	var getUrl = makeUrl(url, {'updateAll' : 'yes'});
	loadTable(getUrl, id);
}
function setPageRequest(url, id, page) {
	var getUrl = makeUrl(url, {'page': page})
	loadTable(getUrl, id);
}
function filterClick(event) {
	$(event.target).select();
}
function loadTable(url, id) {
    var tId = "#" + id;
    showProgress();
    $("#update_"+id).show();
    try {
	    $.get(url, function(data) {
			$(tId).html(data);
			tableEvents();
			$("#update_"+id).hide();
			stopProgress();
	    });
    } catch(err) {
    	$("#update_"+id).hide();
    	stopProgress();
    	alert("Error while loading table contents");
    }
}

function tableEvents() {
	$(".toggleFilterRow").click(function() {
		var tId = $(this).attr("tId");
		var baseUrl = $(this).attr("baseUrl");
		$(".filter_row_"+tId).fadeToggle();
		var url = makeUrl(baseUrl, {filterToggle:"true"});
		$.get(url, function(){});
	});	
	$(".select_pageSize").change(function() {
		var val = $(this).find(":selected").val();
		var tId = $(this).parents("table").attr("id");
		var baseUrl = $(this).attr("baseUrl");
		var getUrl = makeUrl(baseUrl, {'pageSize': val});
		loadTable(getUrl, tId);
	});
	$(".filter_input[type=text]").click(function() {
		$(this).select();
	});
	$(".filter_input[type=text]").keypress(function(event) {
		var tId = $(this).parents("table").attr("id");
		var baseUrl = $(this).parents("tr").attr("baseUrl");
		
		if (event.keyCode == 27) { // escape
			$(event.target).val("");
		}
		if (event.keyCode == 27 || event.keyCode == 13) { // escape or return
			var filters = $(".filter_input");
			var s = "";
			var params = {
				'filter' : 'true'
			};
			$.each(filters, function(idx) {
				var element = $(filters[idx]);
				if (element.attr("type") == "radio") {
					return;
				}
				var val = element.val();
				var c = $(element).parent("td").attr("col");
				if (val != "") {
					params[c] = val;
				}
			});
			var getUrl = makeUrl(baseUrl, params);
			loadTable(getUrl, tId);
		}
	});
	$(".filter_input:radio").click(function() {
		var col = $(this).parent("td").attr("col");
		var tId = $(this).parents("table").attr("id");
		var baseUrl = $(this).parents("tr").attr("baseUrl");
		var filterVal = $(this).val();
		var parameters = {
				'filterVal': filterVal,
				'filterCol': col
		};
		var getUrl = makeUrl(baseUrl, parameters);
		loadTable(getUrl, tId);
	});
	$(".edit").hide();
	$("a.showEditor").click(function() {
		var col = $(this).parents("td").attr("col");
		var tId = $(this).parents("table").attr("id");
		var row = $(this).parents("tr").attr("row");
		var base = "#" + tId + "_" + col + "_" + row;
		$(base + "_value").hide();
		$(base + "_editor").fadeIn();
		$(base + "_editor input").select();
		$(base + "_editor input").focus();
	});
	function submitEdit($this, val) {
		blurEditor($this);
		
		var col = $this.parents("td").attr("col");
		var tId = $this.parents("table").attr("id");
		var row = $this.parents("tr").attr("row");
		var baseUrl = $this.parents("tr").attr("baseUrl");
		var getUrl = makeUrl(baseUrl, {
			'setValue': val,
			'col': col,
			'row': row
		});
		var base = "#" + tId + "_" + col + "_" + row;
		$("#update_"+tId).show();
		$.get(getUrl, function(data) {
			var result = JSON.parse(data);
			if (result.success) {
				$(base + "_valueX").text(result.message);
			} else {
				alert(result.message);
			}
			$("#update_"+tId).hide();
		});
	}
	$("a.submitEditor").click(function() {
		submitEdit($(this), $this.siblings("input").val());
	});
	$(".edit_input").focusout(function() {
		var $this = $(this);
		setTimeout(function() {
			blurEditor($this);
		}, 300)
	});
	$(".edit_input").keydown(function(event) {
		if (event.keyCode == 27) { // escape
			blurEditor($(this));
		} else if (event.keyCode == 13) { // return
			submitEdit($(this), $(this).val());
		}
	});
	function blurEditor(element) {
		var col = element.parents("td").attr("col");
		var tId = element.parents("table").attr("id");
		var row = element.parents("tr").attr("row");
		var base = "#" + tId + "_" + col + "_" + row;
		$(base + "_editor").hide();
		$(base + "_value").fadeIn();
	}
}