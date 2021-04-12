$(function () {
    getStudentName();
    getAreaInfo();
    getTeacherData();

    function getAreaInfo() {

        var getAreaInfoUrl = '/student/get-area-info';
        $.getJSON(getAreaInfoUrl, function (data) {
            if (data.success) {
                $('#point-name-x').html("坐标 X<sub>" + data.point.pointName + "</sub>");
                $('#point-name-y').html("坐标 Y<sub>" + data.point.pointName + "</sub>");
                $('#point-name-h').html("坐标 H<sub>" + data.point.pointName + "</sub>");
                $('#point-name-alpha').html("α<sub>" + data.point.pointName + "-" + data.point.nextPoint + "</sub>");

                $('#area-name').val(data.area.areaName);
                $('#alpha').val(getDMS(data.alpha));
                $('#point-x').val(data.point.pointX);
                $('#point-y').val(data.point.pointY);
                $('#point-h').val(data.point.pointH);

                var thisTime = getDateByTimestamp(data.startTime);
                $('#start-time').val(thisTime);
                thisTime = getDateByTimestamp(data.endTime);
                $('#end-time').val(thisTime);

                var imgUrl = '<img class="area-img" onclick="window.open(this.src,\'area_img\')"  src="/admin/get-image?areaName=' + data.area.areaName + '">';
                $('#area-picture').html(imgUrl);

                var pointDistanceList = data.pointDistanceList;
                var html = '';
                var localPointName=[];
                var localPointId=[];
                pointDistanceList.map(function (item, index) {
                    localPointName[index]=item.name1;
                    localPointId[index]=item.pointId;
                    html += '<div class="row row-point">'
                        + '<div class="col-25">' + item.name1 + '</div>'
                        + '<div class="col-25">' + item.name2 + '</div>'
                        + '<div class="col-50 point-text">' + (item.distance).toFixed(3) + '</div>'
                        + '</div>';
                });
                $('.point-wrap').html(html);
                sessionStorage.setItem("localPointName",localPointName.join());
                sessionStorage.setItem("localPointId",localPointId.join());

            } else {
                // window.location.href = '/student/area-info';
                $.toast('信息查看失败');
            }
        })

    }

    function getTeacherData() {

        var getTeacherDataUrl = '/student/get-teacher-data';
        $.getJSON(getTeacherDataUrl, function (data) {
            if (data.success) {

                var teacherDataList = data.teacherDataList;
                var html = '';
                teacherDataList.map(function (item, index) {
                    html += '<div class="row row-point">'
                        + '<div class="col-50">' + item.teacherDataName + '</div>'
                        + '<a class="col-50"><a href="/student/download-file?id=' + item.teacherDataId + '" download="'+item.teacherDataName+'">下载文件</a></div>'
                        + '</div>';
                });
                $('.teacher-data-wrap').html(html);
            } else {
                // window.location.href = '/student/area-info';
                // $.toast('信息查看失败');
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

});
