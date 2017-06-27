$(function() {
    // 点击登录按钮
    $('#login-bt').click(function() {
        login();
    });
    // 回车事件
    $('#username, #password').keypress(function (event) {
        if (13 == event.keyCode) {
            login();
        }
    });
});
// 登录
function login() {
    $.ajax({
        url: BASE_PATH + '/sso/login',
        type: 'POST',
        data: {
            username: $('#username').val(),
            password: $('#password').val(),
            rememberMe: $('#rememberMe').is(':checked'),
            backurl: BACK_URL
        },
        beforeSend: function() {

        },
        success: function(json){
            if (json.code == 1) {
                location.href = BASE_PATH+json.data;
            } else {
                alert(json.msg);
            }
        },
        error: function(error){
            console.log(error);
        }
    });
}