/**
 *
 */
$(function () {

    var getGroupListUrl = '/teacher/get-groups';
    getTeacherName();
    getGroupList();
    getAllClassName();

    function getGroupList() {
        $.getJSON(getGroupListUrl, function (data) {

            $('#group-year').html(getYearSelect());//获取当前年份，并解析到html中

            if (data.success) {
                var groupList = data.groupList;
                var tmp = '';
                var listSize = groupList.length;
                groupList.map(function (item, index) {

                    var groupName = item.majorCode + " 第" + item.groupCode + "小组";
                    $('#group-name-' + index).val(groupName);
                    $('#group-account-' + index).val(item.groupAccount);
                    $('#group-password-' + index).val(item.password);
                    var stateNum = item.groupState;
                    if (stateNum < 0 || stateNum > 8) {
                        stateNum = 9
                    }
                    if (stateNum == 3) {
                        $('#group-state-' + index).attr("style", "color:red")
                        // } else if (stateNum == 4) {
                        //     $('#group-state-' + index).attr("style", "color:blue")
                    } else if (stateNum == 6) {
                        $('#group-state-' + index).attr("style", "color:red")
                    } else if (stateNum == 8) {
                        $('#group-state-' + index).attr("style", "color:blue")

                    }
                    var stateTag = "#group-state-" + index + " option[data-id ='" + stateNum + "']";
                    $(stateTag).attr('selected', 'selected');
                    var updateTime = getDateByTimestamp(item.updateTime);
                    // var updateTime = new Date(item.updateTime);
                    // updateTime = updateTime.toLocaleDateString();
                    // console.log(updateTime);
                    $('#update-time-' + index).val(updateTime);
                    $('#update-group-' + index).attr("name", item.groupId);
                    $('#group-data-' + index).attr("href", "/teacher/student-data?groupId=" + item.groupId);


                    if (listSize != 0 && index != listSize - 1) {
                        var newIndex = index + 1;
                        var gId = 'group-list-' + (newIndex);
                        var cloneDiv = $('#group-list-0').clone(true);
                        cloneDiv.attr('id', gId);
                        $('.group-content').append(cloneDiv);
                        gId = '#' + gId;
                        $(gId + ' #group-state-0').attr("style", "color:black");
                        $(gId + ' .group-name-class').attr('id', 'group-name-' + newIndex);
                        $(gId + ' .group-account-class').attr('id', 'group-account-' + newIndex);
                        $(gId + ' .group-password-class').attr('id', 'group-password-' + newIndex);
                        $(gId + ' .group-state-class').attr('id', 'group-state-' + newIndex);
                        $(gId + ' .update-time-class').attr('id', 'update-time-' + newIndex);
                        $(gId + ' .update-group-class').attr('id', 'update-group-' + newIndex);
                        $(gId + ' .group-data-class').attr('id', 'group-data-' + newIndex);
                    }


                });
            }
        })
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

        formData.append("groupState", $('#group-state')[0].selectedIndex);
        formData.append("groupCount", $('#group-number')[0].value);
        formData.append("password", $('#group-password').val());
        formData.append("classNumber", $('#class-number')[0].value);
        formData.append("year", $('#group-year')[0].value);
        var majorName = $('#major-name').val();
        if (majorName == null || '' == majorName) {
            $.toast("请输入专业名称！");
            return;
        }
        formData.append("majorName", majorName);

        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/teacher/add-group',
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

    $(document).on("click", "a[class='update-group-class button button-big button-fill button-success']", function (event) {
        var groupId = event.target.name.trim();
        var index = event.srcElement.id.substr(13);//将前十三个字符去掉

        var formData = new FormData();
        if (groupId > 0) {
            formData.append("groupId", groupId);
        } else {
            $.toast('网页未知错误!');
            return;
        }

        var groupState = $('#group-state-' + index).find('option').not(function () {
            return !this.selected;
        }).data('id');
        if (groupState == -1) {
            $.confirm("是否删除账号（信息将丢失）？", function () {
                $.ajax({
                    url: '/teacher/delete-group?groupId=' + groupId,
                    type: 'GET',
                    contentType: false,
                    processData: false,
                    cache: false,
                    success: function (data) {
                        if (data.success) {
                            $.toast('删除成功!');
                            location.reload();
                        } else {
                            $.toast('删除失败：' + data.errMsg);
                        }

                        $('#captcha_img').click();
                    }
                });
            });

        } else {
            formData.append("groupState", groupState);
            var groupPassword = $('#group-password-' + index).val();
            formData.append("groupPassword", groupPassword);


            $.confirm("是否更新？", function () {
                $.ajax({
                    url: '/teacher/update-group',
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
        }

    });

    $(document).on("click", "a[class='class-add button']", function (event) {

        var majorCode = event.target.attributes.majorcode.value;
        var groupCode = event.target.attributes.groupcode.value;
        var formData=new FormData();
        formData.append("majorCode",majorCode);
        formData.append("groupCode",groupCode);

        $.confirm("是否在该班级添加一个小组？", function () {
            $.ajax({
                url: '/teacher/add-class-group',
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
    $(document).on("click", "a[class='class-delete button button-danger']", function (event) {

        var majorCode = event.target.attributes.majorcode.value;
        var formData=new FormData();
        formData.append("majorCode",majorCode);
        $.confirm("是否删除该班级？", function () {
            $.ajax({
                url: '/teacher/delete-class-group',
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

    function getYearSelect() {
        var thisYear = (new Date()).getFullYear();
        var tmpHtmlYear =
            '<option data-id="0">' + (thisYear - 4) + '</option>' +
            '<option data-id="1">' + (thisYear - 3) + '</option>' +
            '<option data-id="2">' + (thisYear - 2) + '</option>' +
            '<option data-id="3">' + (thisYear - 1) + '</option>' +
            '<option data-id="4" selected>' + thisYear + '</option>' +
            '<option data-id="5">' + (thisYear + 1) + '</option>';
        return tmpHtmlYear;


    }

    function getAllClassName() {
        var getClassNameUrl = '/teacher/get-none-history-class-name';
        $.getJSON(getClassNameUrl, function (data) {
            if (data.success) {
                var classNameList = data.classNameList;
                if (classNameList == null) {
                    $.toast('没有班级信息');
                    return;
                }
                var html = '';
                html += '<div class="row row-point">'
                    + '<div class="col-40 center-div-text">班级</div>'
                    + '<div class="col-20 center-div-text">小组数</div>'
                    + '<div class="col-20 center-div-text">增</div>'
                    + '<div class="col-20 center-div-text">删</div>'
                    + '</div>';
                classNameList.map(function (item, index) {
                    html += '<div class="row row-point">'
                        + '<div class="col-40 center-div-text">' + item.majorCode + '</div>'
                        + '<div class="col-20 center-div-text">' + item.groupCode + '</div>'
                        + '<div class="col-20 center-div-text">'
                        + '     <a class="class-add button" majorcode="' + item.majorCode + '" groupcode="' + item.groupCode+'">添加</a>'
                        + '</div>'
                        + '<div class="col-20 center-div-text">'
                        + '     <a class="class-delete button button-danger" majorcode="' + item.majorCode + '">删除</a>'
                        + '</div>'
                        + '</div>';
                });
                $('#class-list').html(html);

            } else {
                window.location.href = '/teacher/area-manage';
                $.toast('信息查看失败');
            }
        })
    }

});

function changePassState(event) {
    if (event.target.checked) {
        $('.group-password-class').removeAttr("disabled");
    } else {
        $('.group-password-class').attr("disabled", "disabled");

    }
}

function changeAddState(event) {
    if (event.target.checked) {
        $('#group-list-add').removeAttr("hidden");
    } else {
        $('#group-list-add').attr("hidden", "hidden");

    }
}

function changeClassState(event) {
    if (event.target.checked) {
        $('#class-list').removeAttr("hidden");
    } else {
        $('#class-list').attr("hidden", "hidden");

    }
}