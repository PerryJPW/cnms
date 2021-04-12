/**
 *
 */
$(function () {

    var getPointListUrl = '/admin/get-points';

    getPointList()

    function getPointList() {
        $.getJSON(getPointListUrl, function (data) {
            if (data.success) {
                var pointList = data.pointList;
                var html = '';
                pointList.map(function (item, index) {
                    html += '<div class="row row-point">'
                        + '<div class="col-10">' + item.pointName + '</div>'
                        + '<div class="col-25 point-text">' + (item.pointX).valueOf().toFixed(3) + '</div>'
                        + '<div class="col-25 point-text">' + item.pointY.toFixed(3) + '</div>'
                        + '<div class="col-20 point-text">' + item.pointH.toFixed(3) + '</div>'
                        + '<div class="col-20 point-text">' + item.distance.toFixed(3) + '</div>'
                        + '</div>';
                });
                $('.point-wrap').html(html);
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
        var pointExcel = $('#point-excel')[0].files[0];
        if (pointExcel == null) {
            $.toast('请选择上传文件!');
            return;
        }
        formData.append("pointExcel", pointExcel);

        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/admin/add-point',
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