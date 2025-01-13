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
          startDate: moment(),
          endDate: moment().add(1, 'days'),
          drops: "auto",
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
        }
      );

      $('.search').on('click', function(){
          if($('input[name="checkInDate"]').val() == "" || $('input[name="checkOutDate"]').val() == ""){
            let startDate = moment();
            let endDate = moment().add(1, 'days');
            let checkInDate = $('input[name="checkInDate"]').val(startDate.format("YYYY-MM-DD"));
            let checkOutDate = $('input[name="checkOutDate"]').val(endDate.format("YYYY-MM-DD"));
            }
      });

      $('.autoplay').slick({
        slidesToShow: 3,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 2000,
      });