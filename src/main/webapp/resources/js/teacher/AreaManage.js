$(function () {

    var getAreaListUrl = '/admin/get-areas';
    getTeacherName();
    getTeacherAreaUsingList();
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
                        + '<div class="col-33 ">' + '<a class="external" href="/teacher/area-info?areaId=' + item.areaId + '">预约</a>' + '</div>'
                        + '</div>';
                });
                $('.area-wrap').html(html);
            }
        })
    }

    function getTeacherAreaUsingList() {
        var gUrl = '/teacher/get-area-use-teacher';
        $.getJSON(gUrl, function (data) {
            if (data.success) {
                var areaUsingList = data.areaUsingList;
                var html = '';
                // console.log(data);
                areaUsingList.map(function (item, index) {

                    html += '<div class="row row-area">'
                        + '<div class="col-33">' + item.areaName + '</div>'
                        + '<div class="col-33">' + item.className + '</div>'
                        + '<div class="col-33 ">' + '<a class="external" href="/teacher/area-info?areaId=' + item.areaId + '">查看</a>' + '</div>'
                        + '</div>';

                });
                $('.area-use-wrap').html(html);
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


});