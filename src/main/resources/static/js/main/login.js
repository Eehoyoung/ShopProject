function sendit() {

    if ($('input[name=loginId]').val() === '') {
        alert('아이디를 입력해주세요');
        $('input[name=loginId]').focus();
        return false;
    }
    if ($('input[name=password]').val() === '') {
        alert('비밀번호를 입력해주세요');
        $('input[name=password]').focus();
        return false;
    }

}

$(function () {
    $("#loginForm").on("submit", function (event) {
        alert("설마?")
        event.preventDefault();

        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        const loginId = $("input[name='loginId']").val();
        const password = $("input[name='password']").val();

        $.ajax({
            url: "/main/login",
            method: "POST",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify({
                loginId: loginId,
                password: password
            }),
            beforeSend: function (xhr) {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            },
            success: function (cookie) {
                alert(cookie)
                document.cookie = `Authorization=${cookie.value};path=/;max-age=${cookie.maxAge};HttpOnly`;
                window.location.href = "/main/index";
            },
            error: function () {
                alert("Invalid username or password");
            }
        });
    });
});