$(function () {

    var getAreaListUrl = '/admin/get-areas';
    getAreaList();

    function getAreaList() {
        $.getJSON(getAreaListUrl, function (data) {
            if (data.success) {
                var areaList = data.areaList;
                var html = '';
                areaList.map(function (item, index) {
                    html += '<div class="row row-area">'
                        + '<div class="col-33">' + item.areaName + '</div>'
                        + '<div class="col-33">' + areaState(item.areaState) + '</div>'
                        // + '<div class="col-25"><img src="/admin' +'"></div>'
                        + '<div class="col-33 ">' + '<a class="external" href="/admin/area-info?areaId=' + item.areaId + '">查看</a>' + '</div>'
                        + '</div>';
                });
                $('.area-wrap').html(html);
            }
        })
    }

    function areaState(stateNum) {
        if (stateNum == 0) {
            return '不可用';
        } else if (stateNum == 1) {
            return '可用';
        } else if (stateNum == 2) {
            return '使用中';
        } else if (stateNum == 3) {
            return '预约中';
        } else {
            return '系统状态异常'
        }

    }

    $('#submit').click(function () {

        var formData = new FormData();
        var images = $('#area-img')[0].files;
        var length = images.length;
        if (length < 1) {
            $.toast('请选择上传文件!');
            return;
        }
        for (var index = 0; index < length; index++) {
            var key = 'areaImg' + index;
            formData.append(key, images[index]);
        }

        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/admin/add-area',
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
});