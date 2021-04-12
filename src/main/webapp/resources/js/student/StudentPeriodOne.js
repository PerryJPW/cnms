$(function () {
    getStudentName();
    getStudentInfo();

    function getStudentInfo() {

        var getStudentInfoUrl = '/student/get-student-info';
        $.getJSON(getStudentInfoUrl, function (data) {
            if (data.success) {
                var gState = data.group.groupState;
                var title = $('#title');
                if (gState == 3) {
                    title.text("P1 已提交");
                } else if (gState == 4) {
                    title.text("P1 不合格");
                    title.attr("style", "color:red");
                }else if (gState > 4) {
                    title.text("P1 合格");
                    title.attr("style", "color:blue")
                }

            } else {
                // window.location.href = '/student/area-info';
                $.toast('信息查看失败');
            }
        })

    }

    $('#submit').click(function () {

        var formData = new FormData();
        var studentPicture = $('#student-picture-upload')[0].files[0];
        if (studentPicture == null) {
            $.toast('请选择上传文件!');
            return;
        }
        formData.append("studentPicture", studentPicture);
        formData.append("pictureState", "1");
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/student/add-picture',
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
    })
});