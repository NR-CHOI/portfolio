function change(){
    var roomInfoId = $("#roomInfoId").val();
    var selectYear = $("#date").val();
    var selectMonth = $("#date > option:selected").attr("value2");
    console.log("roomInfoId: "+roomInfoId);
    console.log("selectYear: "+selectYear);
    console.log("selectMonth: "+selectMonth);

    calendarChange(roomInfoId, selectYear, selectMonth);
}

$('#prev').on('click', function(){
    var prevMonth = $("#date > option:selected").prev().attr("value2");
    console.log("prevMonth: "+ prevMonth);

    $("#date > option:selected").prev().prop("selected", true);

    var roomInfoId = $("#roomInfoId").val();
    var selectYear = $("#date").val();
    var selectMonth = $("#date > option:selected").attr("value2");

    calendarChange(roomInfoId, selectYear, selectMonth);
});

$('#next').on('click', function(){
    var nextMonth = $("#date > option:selected").next().attr("value2");
    console.log("nextMonth: "+ nextMonth);

    $("#date > option:selected").next().prop("selected", true);

    var roomInfoId = $("#roomInfoId").val();
    var selectYear = $("#date").val();
    var selectMonth = $("#date > option:selected").attr("value2");

    calendarChange(roomInfoId, selectYear, selectMonth);
});

$('#today').on('click', function(){
    var selectYear = [[${year}]];
    var selectMonth = [[${month}]];

    var roomInfoId = $("#roomInfoId").val();
    console.log("roomInfoId: "+ roomInfoId);

    $("#date > option[value="+selectYear+"][value2="+selectMonth+"]").prop("selected", true);

    var selectYear = $("#date").val();
    console.log("selectYear: "+ selectYear);

    var selectMonth = $("#date > option:selected").attr("value2");
    console.log("selectMonth: "+ selectMonth);

    calendarChange(roomInfoId, selectYear, selectMonth);
});

function calendarChange(roomInfoId, selectYear, selectMonth){
    $.ajax({
        url: "/room/calendarChange",
        type: "get",
        async: true,
        data: {'roomInfoId':roomInfoId ,'year':selectYear, 'month': selectMonth},
        success:function(result){
        var calendarList = [[${calendarList}]];
        console.log("calendarList: "+calendarList);

        $('.content').remove();
        console.log(result);

        $("#calendar").append(result);

        $("#titleYear").text(selectYear+'년');
        $("#titleMonth").text(selectMonth+'월');

        },
        error: function(){
            console.log('실패');
        }
    });
}