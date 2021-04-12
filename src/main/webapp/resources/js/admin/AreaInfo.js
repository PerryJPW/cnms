$(function () {
    var areaId = getQueryString("areaId");
    getAreaInfo();

    function getAreaInfo() {

        var getAreaInfoUrl = '/admin/get-area-info?areaId=' + areaId;
        $.getJSON(getAreaInfoUrl, function (data) {
            if (data.success) {
                $('#area-name').val(data.area.areaName);
                $('#alpha').val(getDMS(data.alpha));
                var date = getDateByTimestamp(data.area.updateTime);
                $('#update-time').val(date);

                var state = data.area.areaState;
                if (state < 0 || state > 3) {
                    state = 4
                }
                $("#area-state option[data-id ='" + state + "']").attr('selected', 'selected');

                var imgUrl = '<img class="area-img" onclick="window.open(this.src,\'area_img\')" src="/admin/get-image?areaName=' + data.area.areaName + '">';
                $('#area-picture').html(imgUrl);

                var pointList = data.pointList;
                var html = '';
                pointList.map(function (item, index) {
                    html += '<div class="row row-point">'
                        + '<div class="col-10">' + item.pointName + '</div>'
                        + '<div class="col-20 point-text">'
                        + '     <input class="point-value" pointid="' + item.pointId + '" index="1" style="width: 100%" value="' + (item.pointX).toFixed(3) + '">'
                        + '</div>'
                        + '<div class="col-20 point-text">'
                        + '     <input class="point-value" pointid="' + item.pointId + '" index="2" style="width: 100%" value="' + (item.pointY).toFixed(3) + '">'
                        + '</div>'
                        + '<div class="col-20 point-text">'
                        + '     <input class="point-value" pointid="' + item.pointId + '" index="3" style="width: 100%" value="' + (item.pointH).toFixed(3) + '">'
                        + '</div>'
                        + '<div class="col-10">' + item.nextPoint + '</div>'
                        + '<div class="col-20 point-text">' + (item.distance).toFixed(3) + '</div>'
                        + '</div>';
                });
                $('.point-wrap').html(html);


            } else {
                window.location.href = '/admin/area-admin';
                $.toast('信息查看失败');
            }
        })
    }

    function getDMS(alpha) {
        if (alpha < 0) {
            return 'error'
        }
        var re = '';
        var degree = Math.floor(alpha + 0.000000000001);
        re = re + degree + '°';
        var minute = (alpha - Math.floor(alpha)) * 100;
        var minuteF = Math.floor(minute + 0.0000000001);
        if (minuteF < 10) {
            re = re + '0' + minuteF + "'";
        } else {
            re = re + minuteF + "'";
        }
        var second = Math.floor((minute - minuteF + 0.0000000000001) * 100);
        if (second < 10) {
            re = re + '0' + second + '"';
        } else {
            re = re + second + '"';
        }
        return re;
    }


    $('#delete').click(function () {
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        var deleteUrl = '/admin/delete-area?areaId=' + areaId + "&verifyCodeActual=" + verifyCodeActual;

        $.confirm("是否删除？", function () {
            $.ajax({
                url: deleteUrl,
                type: 'GET',
                success: function (data) {
                    if (data.success) {
                        window.location.href = "/admin/area-admin";
                        $.toast('删除成功!');
                    } else {
                        $.toast("删除失败："+data.errMsg);
                    }

                    $('#captcha_img').click();
                }
            });
        })
    });

    $('#update').click(function () {
        var editFlag = false;
        var tmpTag = $('.point-value');
        var editPointIdList = [];
        var editPointValueList = [];
        for (var i = 0; i < tmpTag.length;) {
            if (tmpTag[i].value !== tmpTag[i].defaultValue || tmpTag[i + 1].value !== tmpTag[i + 1].defaultValue
                || tmpTag[i + 2].value !== tmpTag[i + 2].defaultValue) {
                editPointIdList.push(tmpTag[i].attributes.pointid.value);
                editPointValueList.push(tmpTag[i].value);
                editPointValueList.push(tmpTag[i + 1].value);
                editPointValueList.push(tmpTag[i + 2].value);
                editFlag=true;
            }
            i = i + 3;
        }

        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        var formData = new FormData();
        formData.append('editPointIdList', editPointIdList.toString());
        formData.append('editPointValueList', editPointValueList.toString());
        formData.append('verifyCodeActual', verifyCodeActual);
        formData.append('areaId', areaId);

        var areaState = $('#area-state').find('option').not(function () {
            return !this.selected;
        }).data('id');

        formData.append('areaState', areaState);
        if (editFlag) {
            $.confirm("检测到控制点数据有变动，是否继续修改？", function () {
                $.ajax({
                    url: '/admin/update-area',
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
                            $.toast('更新失败:' + data.errMsg);
                        }
                        $('#captcha_img').click();
                    }
                });
            })
        }else {
            $.ajax({
                url: '/admin/update-area',
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
                        $.toast('更新失败:' + data.errMsg);
                    }
                    $('#captcha_img').click();
                }
            });
        }
    })
});