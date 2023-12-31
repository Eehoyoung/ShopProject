$(function () {
    /*클릭 시 nav 나왔다 들어갔다*/
    $('.slidebtn').click(function () {
        if ($(this).hasClass('nav_open')) {
            $(this).parents('nav').css('right', '-200px');
        } else {
            $(this).parents('nav').css('right', '0');
        }
        $(this).toggleClass('nav_open');
        $(this).children('i').toggleClass('fa-times');
    })

    /*클릭 시 스크롤 이동*/
    $('.scrollup').click(function () {
        $('html, body').animate({scrollTop: 0}, 500);
    })
    $('.scrolldown').click(function () {
        $('html, body').animate({scrollTop: $(document).height()}, 500);
    })

    /*로그인 실패 시 alert창 띄우기*/
    if ($('#loginFail').val()) {
        alert('아이디 또는 비밀번호가 일치하지 않습니다');
    }

})

function sendit() {

    alert("여긴가?")
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
    $("#loginNav").on("submit", function (event) {
        alert("nav2.js 실행?")
        event.preventDefault();

        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        const loginId = $("input[name='loginId']").val();
        const password = $("input[name='password']").val();

        $.ajax({
            url: "/main/login",
            method: "POST",
            contentType: 'application/json',
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