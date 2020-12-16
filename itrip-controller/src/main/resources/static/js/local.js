$(function () {
    var message = $("#message").val();
    if (message == "success") {
        $("#myModalLabel").text("激活");
        $('#myModal').modal();
    }

});
$("form").submit(function () {
    var devEmail = $("input[name='devEmail']");
    var devEmailVal = $.trim(devEmail.val());

    var devCode = $("input[name='devCode']");
    var devCodeVal = $.trim(devCode.val());

    var devPassword = $("input[name='devPassword']");
    var devPasswordVal = $.trim(devPassword.val());

    // 邮箱正则匹配
    var reg = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-_.])+[A-Za-z\d]{2,4}$/;

    // 定义一个变量  默认为true
    var flag = true;

    if (devEmailVal == '' || devEmailVal == null) {

        $(".devEmail").html("邮箱不能为空！");
        flag = false;

    } else if (!reg.test(devEmailVal)) {

        $(".devEmail").html("请输入正确格式！");
        flag = false;
    } else {
        $(".devEmail").html("");

    }



    $.ajax({
        type: "POST",
        url: "/itrip/username",
        data: {devCode:devCodeVal},
        dataType: "json",
        success: function (data) {//data：返回数据（json对象）
            alert(data);
            if (data == false) {
                $(".devCode").html("用户名已存在");
                flag = false;
            }else {
               /* $(".devName").html("");*/
            }
        }
    })
    if (devCodeVal == '' || devCodeVal == null) {

        $(".devCode").html("用户名不能为空！");
        flag = false;
    } else {
        $(".devCode").html("");
    }

    if (devPasswordVal == '' || devPasswordVal == null) {

        $(".devPassword").html("密码不能为空！");
        flag = false;

    } else {
        $(".devPassword").html("");

    }

    return flag;
});