//变换验证码
function changeVerifyCode(img) {
    img.src = "../Kaptcha?" + Math.floor(Math.random() * 100)
}

//获取url中参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(%|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return '';
}

//根据时间戳生成的时间对象
function getDateByTimestamp(timestamp) {
    var d = new Date(timestamp);
    var date = '';
    date = (d.getFullYear()) + "-";
    if (d.getMonth() < 9) {
        date = date + "0" + (d.getMonth() + 1) + "-";
    } else {
        date = date + (d.getMonth() + 1) + "-";
    }
    if (d.getDate() < 10) {
        date = date + '0' + (d.getDate());
    } else {
        date = date + (d.getDate());
    }

    return date
}

//获取老师名称
function getTeacherName() {
    var teacherName = sessionStorage.getItem("teacherName");
    $('#teacher-name').text(teacherName + "老师");
}

//获取学生名称
function getStudentName() {
    var studentName = sessionStorage.getItem("studentName");
    $('#student-name').text(studentName);
}

//教师密码修改
$(document).on('click', '.prompt-title-ok', function () {
    $.prompt('请输入登录密码', '密码验证', function (value) {
        var checkPassUrl = '/teacher/check-password?pass=' + value;
        $.getJSON(checkPassUrl, function (data) {
            if (data.success) {
                $.prompt('请输入变更后的密码', '密码更改', function (value) {
                    var changePassUrl = '/teacher/change-password?pass=' + value;
                    $.getJSON(changePassUrl, function (data) {
                        if (data.success) {
                            $.alert('密码修改成功');
                        } else {
                            $.toast('错误' + data.errMsg);

                        }
                    });
                });
            } else {
                $.toast('错误：' + data.errMsg);

            }
        });
        // $.alert('Your name is "' + value + '". You clicked Ok button');
    });
});
