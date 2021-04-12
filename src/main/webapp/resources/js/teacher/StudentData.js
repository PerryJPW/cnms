$(function () {
    var groupId = getQueryString("groupId");
    getStudentDataInfo();

    function getStudentDataInfo() {

        var getStudentDataInfoUrl = '/teacher/get-student-data?groupId=' + groupId;
        var imgHtml = '<img class="student-img" src="/teacher/get-student-picture?groupId=' + groupId + '" onclick="window.open(this.src,\'student_img\')">'
        $('#student-img-content').html(imgHtml);
        var buttonHtml1 =
            '<div class="col-50"><a href="#" class="button button-big button-danger"  id="update-state1-no">P1 不合格</a></div>' +
            '<div class="col-50"><a href="#" class="button button-big button-success" id="update-state1-yes">P1 合格</a></div>';
        $("#student-img-button").html(buttonHtml1);
        var buttonHtml2 =
            '<div class="col-50"><a href="#" class="button button-big button-danger"  id="update-state2-no">P2 不合格</a></div>' +
            '<div class="col-50"><a href="#" class="button button-big button-success" id="update-state2-yes">P2 合格</a></div>';
        $("#student-point-button").html(buttonHtml2);
        $.getJSON(getStudentDataInfoUrl, function (data) {
            if (data.success) {
                var acceptXY = 0.05;
                var acceptH = 0.02;
                acceptXY=data.acceptXY;
                acceptH=data.acceptH;
                var nameStr = data.group.majorCode + " 第" + data.group.groupCode + "小组";
                $('#student-title').text(nameStr);
                var dataList = data.dataList;
                var html1 = '';
                var html2 = '';
                dataList.map(function (item, index) {
                    var colorX = (Math.abs(item.tolerateX) >= acceptXY) ? 'red' : 'black';
                    var colorY = (Math.abs(item.tolerateY) >= acceptXY) ? 'red' : 'black';
                    var colorH = (Math.abs(item.tolerateH) >= acceptH) ? 'red' : 'black';
                    html1 += '<div class="row row-point">'
                        + '<div class="col-25" id="point-' + item.pointId + '">' + '名称' + '</div>'
                        + '<div class="col-25 point-text" style="color:' + colorX + '">' + (item.pointX).toFixed(3) + '</div>'
                        + '<div class="col-25 point-text" style="color:' + colorY + '">' + (item.pointY).toFixed(3) + '</div>'
                        + '<div class="col-25 point-text" style="color:' + colorH + '">' + (item.pointH).toFixed(3) + '</div>'
                        + '</div>';

                    var colorXT = (Math.abs(item.tolerateX) >= acceptXY) ? 'red' : 'gray';
                    var colorYT = (Math.abs(item.tolerateY) >= acceptXY) ? 'red' : 'gray';
                    var colorHT = (Math.abs(item.tolerateH) >= acceptH) ? 'red' : 'gray';
                    html2 += '<div class="row row-point">'
                        + '<div class="col-25" id="tolerate-' + item.pointId + '">' + '名称' + '</div>'
                        + '<div class="col-25 point-text" style="color:' + colorXT + '">' + (item.tolerateX).toFixed(3) * 1000 + '</div>'
                        + '<div class="col-25 point-text" style="color:' + colorYT + '">' + (item.tolerateY).toFixed(3) * 1000 + '</div>'
                        + '<div class="col-25 point-text" style="color:' + colorHT + '">' + (item.tolerateH).toFixed(3) * 1000 + '</div>'
                        + '</div>';
                });

                $('.point-wrap').html(html1);
                $('.tolerate-wrap').html(html2);
                //填充name
                dataList.map(function (item, index) {
                    var getPointNameUrl = '/teacher/get-point-name?pointId=' + item.pointId;
                    $.get(getPointNameUrl, function (data) {
                        if (data.success) {
                            // console.log(data.pointName);
                            $('#point-' + item.pointId).text(data.pointName);
                            $('#tolerate-' + item.pointId).text(data.pointName);
                        } else {
                            $.toast('信息获取失败');
                        }
                    });

                });

            } else {
                // window.location.href = '/admin/area-admin';
                $.toast('信息查看失败');
            }
        });
    }


    $('#update-state1-yes').click(function () {
        var updateUrl = '/teacher/update-group';
        var formData = new FormData();
        formData.append("groupId", groupId);
        formData.append("groupState", "5");
        $.confirm("确认P1 合格？", function () {
            $.ajax({
                url: updateUrl,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    if (data.success) {
                        // window.location.href = "/teacher/student-manage"
                        location.reload();
                        $.toast('P1 合格');
                    } else {
                        $.toast(data.errMsg);
                    }
                }
            });
        })
    });
    $('#update-state1-no').click(function () {
        var updateUrl = '/teacher/update-group';
        var formData = new FormData();
        formData.append("groupId", groupId);
        formData.append("groupState", "4");
        $.confirm("P1 不合格？", function () {
            $.ajax({
                url: updateUrl,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    if (data.success) {
                        // window.location.href = "/teacher/student-manage"
                        location.reload();
                        $.toast('P1 不合格');
                    } else {
                        $.toast(data.errMsg);
                    }
                }
            });
        })
    });
    $('#update-state2-no').click(function () {
        var updateUrl = '/teacher/update-group';
        var formData = new FormData();
        formData.append("groupId", groupId);
        formData.append("groupState", "7");
        $.confirm("P2 不合格？", function () {
            $.ajax({
                url: updateUrl,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    if (data.success) {
                        // window.location.href = "/teacher/student-manage"
                        location.reload();
                        $.toast('P2 不合格');
                    } else {
                        $.toast(data.errMsg);
                    }
                }
            });
        })
    });
    $('#update-state2-yes').click(function () {
        var updateUrl = '/teacher/update-group';
        var formData = new FormData();
        formData.append("groupId", groupId);
        formData.append("groupState", "8");
        $.confirm("确认P2 合格？", function () {
            $.ajax({
                url: updateUrl,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    if (data.success) {
                        // window.location.href = "/teacher/student-manage";
                        location.reload();
                        $.toast('P2 合格，实习完成！');
                    } else {
                        $.toast(data.errMsg);
                    }
                }
            });
        })
    });


});
