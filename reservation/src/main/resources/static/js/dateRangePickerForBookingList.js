$('input[name="dates"]').daterangepicker({
    singleDatePicker: true,
    autoUpdateInput: false,
    locale:{
                format: "YYYY-MM-DD",
                separator: " ~ ",
                applyLabel: "확인",
                cancelLabel: "취소",
                fromLabel: "From",
                toLabel: "To",
                customRangeLabel: "Custom",
                weekLabel: "W",
                daysOfWeek: ["일", "월", "화", "수", "목", "금", "토"],
                monthNames: [
                  "1월",
                  "2월",
                  "3월",
                  "4월",
                  "5월",
                  "6월",
                  "7월",
                  "8월",
                  "9월",
                  "10월",
                  "11월",
                  "12월",
                ]
    }
});

$('input[name="dates"]').on('apply.daterangepicker', function(ev, picker){
    $(this).val(picker.startDate.format('YYYY-MM-DD'));
    console.log("A new date selection was made: " + picker.startDate.format("YYYY-MM-DD") );
});

$('input[name="dates"]').on('cancel.daterangepicker', function(){
    <!-- 시작일/마지막일 취소버튼 누르면 리셋-->
    $(this).val('');
});

$('#startDate').on('apply.daterangepicker', function(){
    <!--시작일 apply했을때 마지막일 disabled 해제-->
    $('#endDate').removeAttr("disabled");
});

$('#startDate').on('cancel.daterangepicker', function(){
    <!-- 시작일 리셋할때 마지막일 disable 설정-->
    $('input[name="dates"]').val('');
    $('#endDate').attr("disabled", true);
});

$('#searchBtn').on('click', function(){
    var memberId = $('#memberId').val();
    console.log("memberId:"+memberId);

    var criteria = $('#criteria').val();
    console.log("criteria:"+criteria);

    var startDate = $('#startDate').val();
    console.log("startDate:"+startDate);

    var endDate = $('#endDate').val();
    console.log("endDate:"+endDate);

    var word = $('#word').val();
    console.log("word:"+word);

     var formCriteria = $('input[name=criteria]').val(criteria);
    console.log("form criteria:"+criteria);

    var formStartDate = $('input[name=startDate]').val(startDate);
    console.log("form startDate:"+startDate);

    var formEndDate = $('input[name=endDate]').val(endDate);
    console.log("form endDate:"+endDate);

    var formWord = $('input[name=word]').val(word);
    console.log("form word:"+word);

    search(memberId, criteria, startDate, endDate, word);
});

function search(memberId, criteria, startDate, endDate, word){
$.ajax({
        url: "/booking/search",
        type: "get",
        async: true,
        data: {'memberId' : memberId,
            'criteria' : criteria, 'startDate': startDate, 'endDate' : endDate, 'word' : word
        },
        success: function(result){
            $('.list').remove();
            $('.join_container').append(result);

        },
        error: function(){
            console.log('실패');
        }
    });
}

function page(value){
    var memberId = $('#memberId').val();
    console.log("memberId:"+memberId);

    var criteria = $('input[name=criteria]').val();
    console.log("form criteria:"+criteria);

    var startDate = $('input[name=startDate]').val();
    console.log("form startDate:"+startDate);

    var endDate = $('input[name=endDate]').val();
    console.log("form endDate:"+endDate);

    var word = $('input[name=word]').val();
    console.log("form word:"+word);

    var page= value;
    console.log("page: "+page);

$.ajax({
        url: "/booking/search",
        type: "get",
        async: true,
        data: {'memberId' : memberId,
            'criteria' : criteria, 'startDate': startDate, 'endDate' : endDate, 'word' : word,
            'page' : page
        },
        success: function(result){
            $('.list').remove();
            $('.join_container').append(result);

        },
        error: function(){
            console.log('실패');
        }
    });
}

function change(){
    var criteria = $('#criteria').val();
    <!--예약번호/예약자 검색시 시작일, 마지막일 비활성화-->
    $('#startDate').removeAttr("disabled");

    if(criteria =="bookingId" ||criteria =="name"){
        console.log("bookingId: "+criteria);
        $('input[name="dates"]').val('');
        $('#startDate').attr("disabled", true);
        $('#endDate').attr("disabled", true);
    }
}


