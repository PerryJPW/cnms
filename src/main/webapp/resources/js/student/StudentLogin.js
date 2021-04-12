$(function () {
    $('#submit').click(function () {

        var formData = new FormData();
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        formData.append('password', $('#password').val());
        formData.append('account', $('#account').val());
        $.ajax({
            url: '/student/check-login',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    sessionStorage.setItem("studentName",data.name);
                    $.toast('登录成功');
                    window.location.href = "/student/area-info"
                } else {
                    $.toast('登录失败' + data.errMsg);
                }

                $('#captcha_img').click();
            }
        });

    });
});