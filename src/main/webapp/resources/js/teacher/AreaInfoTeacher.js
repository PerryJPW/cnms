$(function () {

    var flag = true;
    var areaId = getQueryString("areaId");
    var areaUsingList = [];
    getTeacherName();
    getClassName();
    getAreaInfo();

    function getAreaInfo() {
        var getAreaInfoUrl = '/admin/get-area-info?areaId=' + areaId;
        $.getJSON(getAreaInfoUrl, function (data) {
            if (data.success) {
                var today = new Date();
                today = getDateByTimestamp(today.getTime());
                var title = "场地预约（" + data.area.areaName + "）";
                $('#start-time').val(today);
                $('#end-time').val(today);

                $('#area-apply-title').text(title);
                $('#area-name').val(data.area.areaName);
                $('#alpha').val(getDMS(data.alpha));

                var state = data.area.areaState;
                if (state < 0 || state > 3) {
                    state = 4
                }
                if (state == 0) {
                    flag = false;
                }
                $("#area-state option[data-id ='" + state + "']").attr('selected', 'selected');

                var imgUrl = '<img class="area-img" onclick="window.open(this.src,\'area_img\')" src="/admin/get-image?areaName=' + data.area.areaName + '">';
                $('#area-picture').html(imgUrl);
                areaUsingList = getRealUsingList(data.areaUsingList);
                // console.log(areaUsingList);
                // areaUsingList = data.areaUsingList;
                if (areaUsingList.length > 0) {

                    $('#area-use-content').removeAttr("hidden");
                    $('#area-use-content-title').removeAttr("hidden");
                    var areaUseHtml = "";
                    areaUsingList.map(function (item, index) {
                        areaUseHtml += '<div class="row row-use">'
                            + '<div class="col-100 order-number">' + (index + 1) + '</div>'
                            + '</div>';
                        var teacherName = 'tName';
                        var teacherNameColor = 'gray';
                        var buttonHidden = 'hidden';
                        var className = 'cName';
                        var getTeacherAndClassUrl = '/teacher/get-use-teacher-class?teacherId=' + item.teacherId +
                            '&groupId=' + item.groupId;
                        var xhttp = new XMLHttpRequest();
                        xhttp.onreadystatechange = function () {
                            if (this.readyState == 4 && this.status == 200) {
                                var re = JSON.parse(this.responseText);
                                teacherName = re.teacherName;
                                className = re.className;
                                teacherNameColor = re.ownerUseTeacher ? 'blue' : 'gray';
                                buttonHidden = re.ownerUseTeacher ? ' ' : 'hidden';
                                // console.log(re)
                            }
                        };
                        xhttp.open('GET', getTeacherAndClassUrl, false);
                        xhttp.send();
                        areaUseHtml += '<div class="row row-use">'
                            + '<div class="col-50">使用老师：</div>'
                            + '<div class="col-50 " style="color:' + teacherNameColor + '">' + teacherName + '</div>'
                            + '</div>';
                        areaUseHtml += '<div class="row row-use">'
                            + '<div class="col-50">使用班级：</div>'
                            + '<div class="col-50" style="color: black">' + className + '</div>'
                            + '</div>';
                        areaUseHtml += '<div class="row row-use">'
                            + '<div class="col-50">开始使用时间：</div>'
                            + '<div class="col-50">'
                            + '     <input type="date" id="start-time-' + item.areaUseId + '" value="' + getDateByTimestamp(item.startTime) + '" onchange="changeMyStudentStartTime(event)">'
                            + '</div>'
                            + '</div>';
                        areaUseHtml += '<div class="row row-use">'
                            + '<div class="col-50">结束使用时间：</div>'
                            + '<div class="col-50 ">'
                            + '     <input type="date" id="end-time-' + item.areaUseId + '" value="' + getDateByTimestamp(item.endTime) + '" onchange="changeMyStudentEndTime(event)" >'
                            + '</div>'
                            + '</div>';
                        areaUseHtml += '<div class="row row-use" ' + buttonHidden + '>'
                            + '<div class="col-50">'
                            + '     <a href="#" class="delete-button button button-big button-danger" id="delete-area-use-' + item.areaUseId + '">取消使用</a>'
                            + '</div>'
                            + '<div class="col-50">'
                            + '     <a href="#" class="change-button button button-big button-success" id="change-area-use-' + item.areaUseId + '">更改时间</a>'
                            + '</div>'
                            + '</div>';
                    });
                    $(".area-use-wrap").html(areaUseHtml);

                }

                var pointList = data.pointList;
                var html = '';
                pointList.map(function (item, index) {
                    html += '<div class="row row-point">'
                        + '<div class="col-20">' + item.pointName + '</div>'
                        + '<div class="col-25 point-text">' + (item.pointX).toFixed(3) + '</div>'
                        + '<div class="col-25 point-text">' + (item.pointY).toFixed(3) + '</div>'
                        + '<div class="col-25 point-text">' + (item.pointH).toFixed(3) + '</div>'
                        + '</div>';
                });
                $('.point-wrap').html(html);


            } else {
                window.location.href = '/teacher/area-manage';
                $.toast('信息查看失败');
            }
        })

    }

    //更改按钮响应
    $(document).on("click", "a[class='change-button button button-big button-success']", function (event) {
        var areaUseId = event.srcElement.id.substr(16);//将前十六个字符去掉，获取ID
        var formData = new FormData();
        var areaUsing = {};
        if (areaUseId > 0) {
            var i = -1;
            areaUsingList.map(function (item, index) {
                if (item.areaUseId == areaUseId) {
                    i = index
                }
            });
            //TODO 小BUG：按取消键会使已删除的序列无法恢复
            areaUsingList.splice(i, i + 1);
            // console.log(areaUsingList);
            areaUsing.areaUseId = areaUseId
        } else {
            $.toast('网页未知错误!');
            return;
        }
        // console.log(tmp);
        var startTime = $('#start-time-' + areaUseId)[0].value;
        var endTime = $('#end-time-' + areaUseId)[0].value;
        if (!insureUseDays(startTime, endTime)) {
            $.toast('该段时间不可用!');
            location.reload();
            return;
        }
        areaUsing.startTime = startTime;
        areaUsing.endTime = endTime;
        formData.append('areaUsingString', JSON.stringify(areaUsing));


        $.confirm("是否修改本次场地使用时间？", function () {
            $.ajax({
                url: '/teacher/update-area-use',
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
                }
            });
        });

    });
    //取消按钮响应
    $(document).on("click", "a[class='delete-button button button-big button-danger']", function (event) {
        var areaUseId = event.srcElement.id.substr(16);//将前十六个字符去掉，获取ID
        if (areaUseId > 0) {
        } else {
            $.toast('网页未知错误!');
            return;
        }

        $.confirm("是否退回本次场地的使用？", function () {
            $.ajax({
                url: '/teacher/delete-area-use?areaUseId='+areaUseId,
                type: 'GET',
                success: function (data) {
                    if (data.success) {
                        $.toast('处理成功!');
                        location.reload();
                    } else {
                        $.toast('处理失败：' + data.errMsg);
                    }
                }
            });
        });

    });

    //去除重复的使用列表
    function getRealUsingList(inUsingList) {
        var outUsingList = [];
        var tmpTimeList = [];
        if (inUsingList.length === 0) {
            return outUsingList;
        }
        tmpTimeList[0] = inUsingList[0].startTime;
        outUsingList[0] = inUsingList[0];
        inUsingList.map(function (item, index) {
            // console.log(getDateByTimestamp(item.startTime));
            var flag = false;
            var startTime = item.startTime;
            tmpTimeList.map(function (item1, index1) {
                if (startTime == item1) {
                    flag = true;
                }
            });
            if (!flag) {
                outUsingList.push(item);
                tmpTimeList.push(item.startTime)
            }

        });
        return outUsingList;
    }

    function getClassName() {
        var getClassNameUrl = '/teacher/get-none-area-class-name';
        $.getJSON(getClassNameUrl, function (data) {
            if (data.success) {
                var classNameList = data.classNameList;
                if (classNameList == null) {
                    $.toast('没有未申请场地的班级');
                    return;
                }
                var html = '';
                classNameList.map(function (item, index) {
                    html = html + '<option>' + item + '</option>';
                });
                $('#area-use-class').html(html);

            } else {
                window.location.href = '/teacher/area-manage';
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


    $('#apply-area').click(function () {
        var startTime = $('#start-time')[0].value;
        var endTime = $('#end-time')[0].value;
        if (!insureUseDays(startTime, endTime)) {
            $.toast('该段时间不可用!');
            return;
        }
        if (!flag) {
            $.toast('场地不可用!');
            return;
        }
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码!');
            return;
        }
        var formData = new FormData();
        formData.append('verifyCodeActual', verifyCodeActual);
        var areaUsing = {};
        areaUsing.areaId = areaId;
        areaUsing.startTime = startTime;
        areaUsing.endTime = endTime;
        areaUsing.areaUseState = 1;
        formData.append('areaUsingString', JSON.stringify(areaUsing));
        var majorCode = $('#area-use-class')[0].value;
        formData.append('majorCode', majorCode);

        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/teacher/apply-area',
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

    function insureUseDays(aStartTime, aEndTime) {
        var flag = true;
        areaUsingList.map(function (value, index) {
            var applyStartTimeDate = new Date(aStartTime);
            var applyEndTimeDate = new Date(aEndTime);
            var useStartTimeDate = new Date(value.startTime + 28800000);
            var useEndTimeDate = new Date(value.endTime + 28800000);
            if (applyStartTimeDate === useStartTimeDate
                || applyEndTimeDate === useStartTimeDate
                || applyStartTimeDate === useEndTimeDate
                || applyEndTimeDate === useEndTimeDate) {
                flag = false;
            }
            if ((applyStartTimeDate - useStartTimeDate) == 0) {
                flag = false;
            }
            if ((applyEndTimeDate - useStartTimeDate) == 0) {
                flag = false;
            }
            if ((applyStartTimeDate - useEndTimeDate) == 0) {
                flag = false;
            }
            if ((applyEndTimeDate - useEndTimeDate) == 0) {
                flag = false;
            }

            if (applyStartTimeDate > useStartTimeDate && applyStartTimeDate < useEndTimeDate) {
                flag = false;
            }
            if (applyEndTimeDate > useStartTimeDate && applyEndTimeDate < useEndTimeDate) {
                flag = false;
            }
            if (applyStartTimeDate < useStartTimeDate && applyEndTimeDate > useStartTimeDate) {
                flag = false;
            }

        });
        return flag;

    }
});

function changeClass(event) {
    var className = $('#area-use-class')[0].value;
    var text = className + "使用";
    $('#row-area-use-detail').text(text);
}

function changeStartTime(event) {
    var startTime = event.target.value;
    $('#end-time').val(startTime);
    var className = $('#area-use-class')[0].value;
    var text = className + "使用" + "1天";
    $('#row-area-use-detail').text(text);
}

function changeEndTime(event) {
    var endTime = event.target.value;
    var startTime = $('#start-time')[0].value;
    var day = getUseDays(startTime, endTime);
    if (day < 1) {
        $('#start-time').val(endTime);
        changeStartTime(event);
    } else {
        var className = $('#area-use-class')[0].value;
        var text = className + "使用" + day + "天";
        $('#row-area-use-detail').text(text);
    }
}

function getUseDays(startTime, endTime) {
    var startTimeFormat = new Date(startTime);
    var endTimeFormat = new Date(endTime);
    var days = (endTimeFormat - startTimeFormat) / (24 * 60 * 60 * 1000);
    return days + 1;

}


function changeMyStudentEndTime(event) {
    var timeId=event.srcElement.id.substr(9);
    var endTime = event.target.value;
    var startTime = $('#start-time-'+timeId)[0].value;
    var day = getUseDays(startTime, endTime);
    if (day < 1) {
        $('#start-time-'+timeId).val(endTime);
        $.toast("请重新选择！");
        changeMyStudentStartTime(event);

    }else {
        $.toast("使用" + day + "天");
    }
}

function changeMyStudentStartTime(event) {
    var timeId=event.srcElement.id.substr(11);
    var startTime = event.target.value;
    $('#end-time-'+timeId).val(startTime);
}
