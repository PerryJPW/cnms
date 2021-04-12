$(function () {

    getStudentName();
    getStudentInfo();
    setTable();
    getPointData();


    function getStudentInfo() {

        var getStudentInfoUrl = '/student/get-student-info';
        $.getJSON(getStudentInfoUrl, function (data) {
            if (data.success) {
                var gState = data.group.groupState;
                var title = $('#title');
                if (gState == 6) {
                    title.text("P2 已提交");
                    // title.attr("style", "color:green");
                } else if (gState == 7) {
                    title.text("P2 不合格");
                    title.attr("style", "color:red");
                }else if (gState == 8) {
                    title.text("P2 合格");
                    title.attr("style", "color:blue")
                }
            } else {
                // window.location.href = '/student/area-info';
                $.toast('信息查看失败');
            }
        })

    }

    function getPointData() {
        var getPointDataUrl = '/student/get-point-data';
        $.getJSON(getPointDataUrl, function (data) {
            if (data.success) {
                var dataList = data.dataList;
                // var passFlag = true;
                dataList.map(function (item, index) {
                    // var pState = item.studentDataState;
                    // if (pState != 1) {
                    //     passFlag = false;
                    // }
                    $('#point-data-x-' + index).val(item.pointX.toFixed(3));
                    $('#point-data-y-' + index).val(item.pointY.toFixed(3));
                    $('#point-data-h-' + index).val(item.pointH.toFixed(3));
                });
                // var title = $('#title');
                // if (!passFlag) {
                //     title.text("阶段二-数据不合格");
                //     title.attr("style", "color:red;");
                // }
            } else {
                $.toast('信息查看失败');
            }

        });
    }

    function setTable() {
        var pointName = sessionStorage.getItem("localPointName").split(',');
        pointName.map(function (item, index) {
            $('#point-name-x-' + index).html('X <sub>' + item + '</sub>');
            $('#point-name-y-' + index).html('Y <sub>' + item + '</sub>');
            $('#point-name-h-' + index).html('H <sub>' + item + '</sub>');

            if (pointName.length != 0 && index != pointName.length - 1) {
                var newIndex = index + 1;
                var pId = 'point-list-' + (newIndex);
                var cloneDiv = $('#point-list-0').clone(true);
                cloneDiv.attr('id', pId);
                $('.point-content').append(cloneDiv);
                pId = '#' + pId;
                $(pId + ' .point-name-x-class').attr('id', 'point-name-x-' + newIndex);
                $(pId + ' .point-data-x-class').attr('id', 'point-data-x-' + newIndex);
                $(pId + ' .point-name-y-class').attr('id', 'point-name-y-' + newIndex);
                $(pId + ' .point-data-y-class').attr('id', 'point-data-y-' + newIndex);
                $(pId + ' .point-name-h-class').attr('id', 'point-name-h-' + newIndex);
                $(pId + ' .point-data-h-class').attr('id', 'point-data-h-' + newIndex);

            }
        });
    }

    $('#submit').click(function () {
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        var formData = new FormData();
        formData.append('verifyCodeActual', verifyCodeActual);
        var pointName = sessionStorage.getItem("localPointName").split(',');
        var pointId = sessionStorage.getItem("localPointId").split(',');
        formData.append("pointCount", pointName.length.toString());

        var flag = true;
        pointName.map(function (item, index) {
            var px = $('#point-data-x-' + index).val();
            var py = $('#point-data-y-' + index).val();
            var ph = $('#point-data-h-' + index).val();
            if (px == null || py == null || ph == null || px == "" || py == "" || ph == "") {
                $.toast("请将数据输入完整");
                flag = false;
                return;
            }
            var studentData = {};
            studentData.pointId = pointId[index];
            studentData.pointX = parseFloat(px).toFixed(3);
            studentData.pointY = parseFloat(py).toFixed(3);
            studentData.pointH = parseFloat(ph).toFixed(3);
            // studentData.studentDataState = 1;
            // studentData.updateTime = new Date();
            formData.append("studentData-" + index, JSON.stringify(studentData));
        });
        if (!flag) {
            $('#captcha_img').click();
            return;
        }

        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/student/add-point-data',
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