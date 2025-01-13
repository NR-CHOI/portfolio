$('.navBtn li a').on('click',function(){
    var target = $(this).attr('id');
    console.log("target: "+target);

    var link = $(this);
    var item = link.parent("li");

        if (item.hasClass("active")) {
            item.removeClass("active").children("a").removeClass("active");
        } else {
            item.addClass("active").children("a").addClass("active");
        }
});
