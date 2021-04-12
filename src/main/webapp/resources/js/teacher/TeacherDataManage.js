$(function () {

    var getTeacherDataListUrl = '/teacher/get-data';
    getTeacherName();
    getTeacherDataList();


    function getTeacherDataList() {
        $.getJSON(getTeacherDataListUrl, function (data) {
            if (data.success) {
                var dataList = data.dataList;
                var html = '';

                dataList.map(function (item, index) {
                    html +=
                        '<div class="row row-teacher">' +
                        '    <div class="col-25">' + item.teacherDataName + '</div>' +
                        '    <div class="col-25">' + "正常" + '</div>' +
                        '    <div class="col-25">' +getDateByTimestamp(item.updateTime)  + '</div>' +
                        '    <div class="col-25">' +
                        '        <a href="/student/download-file?id='  + item.teacherDataId + '" download="'+item.teacherDataName+ '">下载</a>' +
                        '        <a href="#" class="delete-data" id="deleta-data-'+item.teacherDataId +'">删除</a>' +
                        '    </div>' +
                        '</div>';
                });
                $('.teacher-wrap').html(html);
            }
        });
    }

    $(document).on("click", "a[class='delete-data']", function (event) {
        var teacherDataId = event.srcElement.id.substr(12);
        var deleteUrl = '/teacher/delete-data';

        if (teacherDataId > 0) {
            deleteUrl = deleteUrl + '?teacherDataId=' + teacherDataId;
        } else {
            return;
        }

        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        deleteUrl = deleteUrl + '&verifyCodeActual=' + verifyCodeActual;

        $.confirm("是否删除？", function () {
            $.ajax({
                url: deleteUrl,
                type: 'GET',
                success: function (data) {
                    if (data.success) {
                        $.toast('处理成功!');
                        location.reload();
                    } else {
                        $.toast('处理失败：' + data.errMsg);
                    }
                }
            });
        });

    });


    $('#submit').click(function () {

        var formData = new FormData();
        var teacherFile = $('#teacher-data-file')[0].files[0];
        if (teacherFile == null) {
            $.toast('请选择上传文件!');
            return;
        }
        formData.append("teacherDataFile", teacherFile);

        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        var teacherDataState = $('#teacher-data-state').find('option').not(function () {
            return !this.selected;
        }).data('id');
        formData.append('teacherDataState', teacherDataState);
        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/teacher/add-data',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    $.toast('处理成功!');
                    location.reload();
                } else {
                    $.toast('处理失败：' + data.errMsg);
                }

                $('#captcha_img').click();
            }
        });

    });

})