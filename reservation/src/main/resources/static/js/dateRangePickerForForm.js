 $('#status').click(function(){
    var checked = $('#status').is(':checked');
    console.log("checked : "+checked);

    if(checked == false){
        $('.form-select, .form-control').addClass('close');
    }else{
        $('.form-select, .form-control').removeClass('close');
    }
});


 $('#startDate').daterangepicker({
    singleDatePicker: true,
    showDropdowns: true,
    startDate: $('#startDate').val(),
    minDate: moment(),
    locale:{
        format: "YYYY-MM-DD",
    }
    }, function(start, end, label) {
    var startDate = $('#startDate').val(start.format("YYYY-MM-DD"));
    console.log("start: " + start.format("YYYY-MM-DD"));
});

$('#startDate').on('hide.daterangepicker', function(ev, picker){
    end();
});

end();

function end(){
    $('#endDate').daterangepicker({
        singleDatePicker: true,
        showDropdowns: true,
        autoUpdateInput: false,
        minDate: $('#startDate').val(),
        locale:{
            format: "YYYY-MM-DD",
        }
        }, function(start, end, label) {
        $('#endDate').val(end.format("YYYY-MM-DD"));
        console.log("end: " + end.format('YYYY-MM-DD'));
        });
}

if($('#price').val() == 0){
    $('#price').val('');
}

if($('#quantity').val() == 0){
    $('#quantity').val('');
}
