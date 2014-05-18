$(document).ready(function() {
	// initially hide the menu
	$("#menuContent").css({left: "-180px"});
	
    // Mouseover for links in top bar
    $("#top a").mouseenter(function() {
        //$(this).animate({borderBottomColor : '#a5ffff'}, 300);
        $(this).animate({backgroundColor : '#dcdcdc'}, 300);
    });
    $("#top a").mouseleave(function() {
        //$(this).animate({borderBottomColor : 'transparent'}, 300);
    	$(this).animate({backgroundColor : 'transparent'}, 300);
    });
    
    
    // Mouseover for in-header menu links
    $(".menuGroup a").mouseenter(function() {
        $(this).animate({borderLeftColor : '#bfbfbf'}, 300);
    });
    $(".menuGroup a").mouseleave(function() {
        $(this).animate({borderLeftColor : 'transparent'}, 300);
    });
    
    
    
    // Main menu fade in
    // hide the menu after 2seconds when the page was loaded
    var timer = setTimeout(function() {
    	hideMenu();
    }, 2000);
    
    $("#menuContent").mouseenter(function() {
    	showMenu();
    	if (timer) {
    		clearTimeout(timer);
    	}
    });
    $("#menuContent").mouseleave(function() {
    	timer = setTimeout(function() {
        	hideMenu();
    	}, 300);
    });
    
    
    
    // Fade in and out for menu descriptions
    $(".menuEntry").mouseenter(function() {
        var key = $(this).attr("id");
        var description = DESCRIPTIONS[key];
        $("#menuDescription").text(description);
        $("#menuDescription").show();
    });
    $(".menuEntry").mouseleave(function() {
        var key = $(this).attr("id");
        $("#menuDescription").text("");
    });
    
    
    
    // User menu fade in and out
    $("#userMenu").mouseenter(function() {
        $("#userMenuUl").fadeIn(300);
    });
    $("#userMenu").mouseleave(function() {
        $("#userMenuUl").fadeOut(300);
    });
});



// HELPER SECTION

// Shows the in-header menu by fading it in
function showMenu() {
    $("#menuContent").animate({left: "0px"}, 200);
}

// Hides the in-header menu by fading it out
function hideMenu() {
	$("#menuContent").animate({left: "-180px"}, 200);
}


function fadeToggleId(id) {
	$("#"+id).find("tbody").fadeToggle();
}