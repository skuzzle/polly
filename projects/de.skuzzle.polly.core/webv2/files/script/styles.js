$(document).ready(function() {
    // Mouseover for links in top bar
    $("#top a").mouseenter(function() {
        $(this).animate({borderBottomColor : '#3333ff'}, 300);
    });
    $("#top a").mouseleave(function() {
        $(this).animate({borderBottomColor : 'transparent'}, 300);
    });
    
    
    
    // Mouseover for social links in header
    $("#headerContent a").mouseenter(function() {
        $(this).animate({borderBottomColor : 'white'}, 300);
    });
    $("#headerContent a").mouseleave(function() {
        $(this).animate({borderBottomColor : 'transparent'}, 300);
    });
    
    
    
    // Mouseover for in-header menu links
    $(".menuGroup a").mouseenter(function() {
        $(this).animate({borderLeftColor : 'white'}, 300);
    });
    $(".menuGroup a").mouseleave(function() {
        $(this).animate({borderLeftColor : 'transparent'}, 300);
    });
    
    
    
    // Header menu fade in
    var hover = false;
    var timer;
    $(".showContent").mouseleave(function() {
        timer = setTimeout(function() {
            if (!hover) {
                hideMenu();
            }
        }, 150);
    });
    $(".showContent").mouseenter(function() {
        hover = false;
        showMenu();
    });
    $("#menuContent").mouseenter(function() {
        hover = true;
    });
    $("#menuContent").mouseleave(function() {
        hideMenu()
        clearTimeout(timer);
    });
    
    
    
    // Fade in and out for menu descriptions
    $(".menuEntry").mouseenter(function() {
        var key = $(this).attr("id");
        var description = DESCRIPTIONS[key];
        $("#menuDescription").text(description);
        $("#menuDescription").fadeIn();
    });
    $(".menuEntry").mouseleave(function() {
        var key = $(this).attr("id");
        $("#menuDescription").hide();
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
    $("#menuContent").fadeIn(250);
    $(".showContent").animate({borderBottomColor : '#3333ff'}, 250);
}

// Hides the in-header menu by fading it out
function hideMenu() {
    $(".showContent").animate({borderBottomColor : 'transparent'}, 250);
    $("#menuContent").fadeOut(250);
}