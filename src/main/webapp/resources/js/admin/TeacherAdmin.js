/**
 *
 */
$(function () {

    var getTeacherListUrl = '/admin/get-teachers';
    getTeacherList()

    function getTeacherList() {
        $.getJSON(getTeacherListUrl, function (data) {
            if (data.success) {
                var teacherList = data.teacherList;
                var html = '';
                teacherList.map(function (item, index) {
                    html +=
                        '<div class="row row-teacher">' +
                        '    <div class="col-20">' + item.teacherName + '</div>' +
                        '    <div class="col-20">' + setAccountTag(item.teacherId, item.teacherAccount) + '</div>' +
                        '    <div class="col-20">' + setPasswordTag(item.teacherId, item.password) + '</div>' +
                        '    <div class="col-20">' + teacherState(item.teacherId,item.teacherState) + '</div>' +
                        '    <div class="col-20 ">' + operationButton(item.teacherId) + '</div>' +
                        '</div>';
                });
                $('.teacher-wrap').html(html);
            }
        })
    }

    $(document).on("click", "a[class='update-teacher']", function (event) {
        var teacherId = event.srcElement.id.substr(15);
        teacherId = teacherId.trim();

        var formData = new FormData();
        if (teacherId > 0) {
            formData.append("teacherId", teacherId);
        } else {
            $.toast('网页未知错误!');
            return;
        }
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入下方的验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        var pass = $('#teacher-password-' + teacherId).val();
        if (pass == null || pass == "") {
            $.toast("请输入密码！");
            return;
        } else {
            formData.append("password", pass);
        }
        var account = $('#teacher-account-' + teacherId).val();
        if (account == null || account == "") {
            $.toast("请输入账户名！");
            return;
        } else {
            formData.append("teacherAccount", account);
        }
        var teacherState = $('#teacher-state-'+teacherId).find('option').not(function () {
            return !this.selected;
        }).data('id');
        formData.append("teacherState", teacherState)

        $.confirm("是否更新？", function () {
            $.ajax({
                url: '/admin/update-teacher',
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


    $(document).on("click", "a[class='delete-teacher']", function (event) {
        var teacherId = event.srcElement.id.substr(15);
        var deleteUrl = '/admin/delete-teacher';

        if (teacherId > 0) {
            deleteUrl = deleteUrl + '?teacherId=' + teacherId;
        } else {
            return;
        }

        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入下方的验证码!');
            return;
        }
        deleteUrl = deleteUrl + '&verifyCodeActual=' + verifyCodeActual;

        $.confirm("是否删除？", function () {
            $.ajax({
                url: deleteUrl,
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


    $('#add-one-teacher').click(function () {
        var teacher = {};
        var name = $('#teacher-name').val();
        if (name == null || name == '') {
            $.toast("请输入名字");
            return;
        }
        teacher.teacherName = name;
        teacher.teacherAccount = $('#teacher-account').val();
        teacher.password = $('#teacher-password').val();
        teacher.teacherState = $('#teacher-state').find('option').not(function () {
            return !this.selected;
        }).data('id');

        var formData = new FormData();
        formData.append('teacherString', JSON.stringify(teacher));
        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入下方的验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);

        $.ajax({
            url: '/admin/add-teacher',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    $.toast('添加成功!');
                    location.reload();
                } else {
                    $.toast('添加失败' + data.errMsg);
                }
                $('#captcha_img').click();
            }
        });


    });


    $('#submit').click(function () {

        var formData = new FormData();
        var teacherExcel = $('#teacher-excel')[0].files[0];
        if (teacherExcel == null) {
            $.toast('请选择上传文件!');
            return;
        }
        formData.append("teacherExcel", teacherExcel);


        //验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入下方的验证码!');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        $.toast('上传中，请等待系统处理。。。');
        $.ajax({
            url: '/admin/add-teacher-excel',
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

    function teacherState(teacherId,stateNum) {
        var htmlStr = '<select class="teacher-state" id="teacher-state-'+teacherId+'">';
        if (stateNum == 0) {
            htmlStr = htmlStr +
                '<option data-id="0" selected>不可用</option>' +
                '<option data-id="1">正常</option>' +
                '<option data-id="2" disabled hidden>系统异常</option>'
        } else if (stateNum == 1) {
            htmlStr = htmlStr +
                '<option data-id="0" >不可用</option>' +
                '<option data-id="1" selected>正常</option>' +
                '<option data-id="2" disabled hidden>系统异常</option>'

        } else {
            htmlStr = htmlStr +
                '<option data-id="0" >不可用</option>' +
                '<option data-id="1" >正常</option>' +
                '<option data-id="2" selected disabled>系统异常</option>'
        }
        htmlStr = htmlStr + '</select>';
        return htmlStr

    }

    function setPasswordTag(teacherId, password) {
        var html = '<input class="teacher-input" type="text" id="teacher-password-' + teacherId
            + '" placeholder="请输入密码" value="' + password + '">';
        return html;
    }

    function setAccountTag(teacherId, account) {
        var html = '<input class="teacher-input" type="text" id="teacher-account-' + teacherId
            + '" placeholder="请输入账户名" value="' + account + '">';
        return html;
    }

    function operationButton(teacherId) {
        var htmlStr = '';
        htmlStr = htmlStr +
            '<a href="#" class="update-teacher" id="update-teacher-' + teacherId + '">更新</a>' +
            '<b>  </b>' +
            '<a href="#" class="delete-teacher" id="delete-teacher-' + teacherId + '">删除</a>';
        return htmlStr;

    }
})