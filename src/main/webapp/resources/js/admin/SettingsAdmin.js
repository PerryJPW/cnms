$(function () {
    getSettingsInfo();

    function getSettingsInfo() {

        var getSettingsInfoUrl = '/admin/get-settings';
        $.getJSON(getSettingsInfoUrl, function (data) {
            if (data.success) {
                $('#admin-pass').val(data.adminPassword);
                $('#tolerance-xy').val(data.acceptXy.toFixed(3));
                $('#tolerance-h').val(data.acceptH.toFixed(3));

            } else {
                $.toast('信息查看失败');
            }
        })

    }

    $('#update').click(function () {
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        var formData = new FormData();
        formData.append('verifyCodeActual', verifyCodeActual);
        var settings = {};
        settings.adminPassword = $('#admin-pass').val();
        settings.acceptXy = $('#tolerance-xy').val();
        settings.acceptH = $('#tolerance-h').val();

        formData.append("settingsString", JSON.stringify(settings));
        $.confirm("是否更新？", function () {
            $.ajax({
                url: '/admin/update-settings',
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    if (data.success) {
                        $.toast('更新成功!');
                        location.reload();
                    } else {
                        $.toast('更新失败' + data.errMsg);
                    }
                    $('#captcha_img').click();
                }
            });
        });
    });
    $('#end-term').click(function () {
        var str = $('#ensure-end').val();
        if (str != "确认结束本学期") {
            $.toast('请输入 确认结束本学期');
            return;
        }
        $.confirm("是否结束本学期？", function () {
            $.ajax({
                url: '/admin/end-term',
                type: 'GET',
                success: function (data) {
                    if (data.success) {
                        $.toast('成功!');
                        // location.reload();
                        window.location.href = '/admin/area-admin';
                    } else {
                        $.toast('失败 ' + data.errMsg);
                    }
                    $('#captcha_img').click();
                }
            });
        });
    });
});