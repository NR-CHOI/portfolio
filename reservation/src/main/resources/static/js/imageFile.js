$("#fileBox").on("click", ".removeBtn", function(){
    $(this).parent().remove();
});

$("#plusBtn").on("click", function(){
    var length = $("#fileBox").children("div").length;
    var i= length+1;
    console.log("i: "+i);
    var element = $('<div><input class="upload-name" value="첨부파일" placeholder="첨부파일"><label class="btn btn-secondary btn-sm" for="file'+i+'">파일찾기</label><input type="file" name="imageFiles" id="file'+i+'" onchange="changeFile(this)" style="display:none;"><input type="button" class="btn btn-secondary btn-sm removeBtn" value="삭제" style="margin-left:5px"></div>');
    $("#fileBox").append(element);
});

function changeFile(ths){
    //이미지파일 체크
    var fileVal = $(ths).val();
    if(fileVal != ""){
        var ext = $(ths).val().split('.').pop().toLowerCase();
            if($.inArray(ext, ['jpg', 'jpeg', 'gif', 'png']) == -1){
                alert("이미지 파일만 업로드 할 수 있습니다.");
                $(ths).val('');
                return;
            }
        }

//  용량체크
    var maxSize = 1024 * 1024;  //1byte=1024kb, 1MB = 1024kb
    var fileSize = ths.files[0].size;
    console.log("fileSize: "+fileSize);
    if(fileSize > maxSize){
        alert("첨부파일 사이즈는 1MB 이내로 등록 가능합니다.");
        $(ths).val('');
        return;
    }

    var fileName = $(ths).val().split('/').pop().split('\\').pop();
    console.log("fileName: "+fileName);
    var fileChange = $(ths).parent().children(".upload-name").val(fileName);
}

$(".removeButton").on('click', function(){
    var removeImage = $(this).parent().children("span").attr("id");
    console.log("removeImage: "+removeImage);
    $(this).parent().children('input[name="deleteImage"]').val(removeImage);
    var imageNum =$(this).parent().children('input[name="deleteImage"]').val();
    console.log("imageNum: "+imageNum);
    $(this).parent().css("display","none");
});