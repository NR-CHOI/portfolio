function count(type, ths){
    var target = $(ths).attr('class');
    console.log("target: "+target);
    var number = $("input[name='"+target+"']").val();

    if(type == 'minus'){
         if(number>1){
            number = parseInt(number) -1;
        }else{
            $(ths).attr("disabled", true);
        }

    }else if(type == 'plus'){
        $('.'+target).attr("disabled", false);
        number = parseInt(number) +1;
    }
    console.log("number3: "+number);
    var result = $("input[name='"+target+"']").val(number);
    console.log("result: "+result.val());
}

$('input[name="daterange"]').daterangepicker({
          locale: {
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
          },
          minDate: moment(),
          maxDate: moment().add(1, 'years'),
          startDate: $('input[name="checkInDate"]').val(),
          endDate: $('input[name="checkOutDate"]').val(),
        },
        function (start, end, label) {
          let checkInDate = $('input[name="checkInDate"]').val(start.format("YYYY-MM-DD"));
          let checkOutDate = $('input[name="checkOutDate"]').val(end.format("YYYY-MM-DD"));
          console.log(
            "A new date selection was made: " +
              start.format("YYYY-MM-DD") +
              " to " +
              end.format("YYYY-MM-DD")
          );
        });


$('.search').on('click', function(){
    if($('input[name="checkInDate"]').val() == "" || $('input[name="checkOutDate"]').val() == ""){
        let startDate = moment();
        let endDate = moment().add(1, 'days');
        let checkInDate = $('input[name="checkInDate"]').val(startDate.format("YYYY-MM-DD"));
        let checkOutDate = $('input[name="checkOutDate"]').val(endDate.format("YYYY-MM-DD"));
        }
});


function change(e){
    var length = $('#roomList > div').length;
    console.log("length: "+length);

    var sumCount =0;
    var sumPrice =0;
    for(var i=0; i<length; i++){
        var count= parseInt($("#BookingFormList\\["+i+"\\]\\.count").val());
        console.log("원래 count:"+count);
        sumCount = sumCount + count;
        console.log("객실 갯수:"+sumCount);

        var price= parseInt($("#BookingFormList\\["+i+"\\]\\.price").val());
        console.log("원래 price:"+price);
        sumPrice = sumPrice + (count*price);
        console.log("count * price :"+sumPrice);
        }
    $('#selectText > #add').remove();
    if(sumCount !=0){
        $('#selectText').prepend("<div class=\"text-body-secondary lh-sm\" id=\"add\">객실 <strong>"
        +sumCount+"</strong>개 요금</br><h4>"+sumPrice+"원</h4></div>");
    }
}

$('.submit_btn').on('click', function(e){
        var length = $('#roomList > div').length;
        var sumCount =0;
        var sumPrice =0;
        for(var i=0; i<length; i++){
            var count= parseInt($("#BookingFormList\\["+i+"\\]\\.count").val());
            sumCount = sumCount + count;
            }
        if(sumCount ==0){
            e.preventDefault();
            $('.form-select').focus();
            $('#selectText > #add').remove();
            $('#selectText').prepend("<div id=\"add\" style=\"color : #be0000;\">예약하고자 하는 객실을 하나 이상 선택하세요.</div>")
        }
});

function more(){

    var btn = $("#btnValue").val();
    console.log("btn: "+btn);

    var nextPage = parseInt(btn)+1;
    console.log("nextPage: "+nextPage);

    $("#btnValue").attr('value', nextPage);

    var btnValue = $("#btnValue").val()
    console.log("btnValue: "+btnValue);

    var checkInDate = $("#checkIn").val();
        console.log("checkInDate: " + checkInDate);
    var checkOutDate = $("#checkOut").val();
        console.log("checkOutDate: " + checkOutDate);
    var people = $("#peopleNum").val();
        console.log("people: " + people);
    var quantity = $("#quantityNum").val();
        console.log("quantity: " + quantity);
    var stayDate = $("#stayDateNum").val();
        console.log("stayDate: " + stayDate);

    $.ajax({
            url: "/room/searchMore",
            type: "get",
            async: true,
            data: {
            'page': nextPage,
            'checkInDate': checkInDate,
            'checkOutDate': checkOutDate,
            'people': people,
            'quantity': quantity,
            'stayDate': stayDate
            },
            success:function(result){
                console.log(result);
                $('#roomList').append(result);

                var hasNext = $('#roomList').children().last().find('#hasNext').val();
                console.log("hasNext: "+hasNext);
                if(hasNext == 'false') {
                    $('#btnValue').hide();
                }
            },
            error: function(){
                console.log('실패');
            }
        });
}




