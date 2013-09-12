function sortTableRequest(url, id, col) {
    var getUrl = url + "?sort="+col;
    loadTable(getUrl, id);
}
function filterTableRequest(event) {
	if (event.which != 13 && event.which != 27) {
		return;
	}
	var tId = $(event.target).attr("tableId");
	var filterCol = $(event.target).attr("col");
	var filterVal = encodeURIComponent($(event.target).val());
	if (event.which == 27) {
		filterVal = "";
	}
	var getUrl = $(event.target).attr("filterUrl") + 
		"?filterVal="+filterVal+"&filterCol="+filterCol;
	loadTable(getUrl, tId);
}
function setPageRequest(url, id, page) {
	var getUrl = url + "?page="+page;
	loadTable(getUrl, id);
}
function filterClick(event) {
	$(event.target).select();
}
function loadTable(url, id) {
    var tId = "#" + id;
    $.get(url, function(data) {
        $(tId).html(data);
        tableEvents();
    });
}

function tableEvents() {
	$(".select_pageSize").change(function() {
		var val = $(this).find(":selected").val();
		var tId = $(this).parents("table").attr("id");
		var baseUrl = $(this).attr("baseUrl");
		var getUrl = baseUrl+"?pageSize="+val;
		loadTable(getUrl, tId);
	});
	$(".filter_input[type=text]").click(function() {
		$(this).select();
	});
	$(".filter_input[type=text]").keypress(function(event) {
		var col = $(this).parent("td").attr("col");
		var tId = $(this).parents("table").attr("id");
		var baseUrl = $(this).parents("tr").attr("baseUrl");
		var filterVal = $(this).val();
		if (event.which == 0) {
			filterVal = "";
		}
		if (event.which == 0 || event.which == 13) {
			var getUrl = baseUrl + "?filterVal="+filterVal+"&filterCol="+col;
			loadTable(getUrl, tId);
		}
	});
	$(".filter_input:radio").click(function() {
		var col = $(this).parent("td").attr("col");
		var tId = $(this).parents("table").attr("id");
		var baseUrl = $(this).parents("tr").attr("baseUrl");
		var filterVal = $(this).val();
		var getUrl = baseUrl + "?filterVal="+filterVal+"&filterCol="+col;
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
	$("a.submitEditor").click(function() {
		blurEditor($(this));
		
		var col = $(this).parents("td").attr("col");
		var tId = $(this).parents("table").attr("id");
		var row = $(this).parents("tr").attr("row");
		var baseUrl = $(this).parents("tr").attr("baseUrl");
		var val = $(this).siblings("input").val();
		var getUrl = baseUrl+"?setValue="+val+"&col="+col+"&row="+row;
		var base = "#" + tId + "_" + col + "_" + row;
		$.get(getUrl, function(data) {
			var result = JSON.parse(data);
			if (result.success) {
				
			} else {
				alert(result.message);
			}
		});
	});
	$(".edit_input").focusout(function() {
		var $this = $(this);
		setTimeout(function() {
			blurEditor($this);
		}, 300)
	});
	$(".edit_input").keydown(function(event) {
		if (event.which == 27) {
			blurEditor($(this));
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