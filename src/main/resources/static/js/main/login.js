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
                document.cookie = `Authorization=${cookie.value};path=/;max-age=${cookie.maxAge};HttpOnly`;
                window.location.href = "/main/index";
            },
            error: function () {
                alert("Invalid username or password");
            }
        });
    });
});

function openFindIdPopup() {
    document.getElementById("findIdPopup").style.display = "block";
}

function closeFindIdPopup() {
    document.getElementById("findIdPopup").style.display = "none";
}

function findId() {
    const name = document.getElementById("name").value;
    const phoneNum = document.getElementById("phoneNum").value;

    if (name && phoneNum) {
        fetchIdFromServer(name, phoneNum);
    } else {
        alert("이름과 전화번호를 입력하세요.");
    }
}

async function fetchIdFromServer(name, phoneNum) {
    const csrfToken = document.getElementsByName("_csrf")[0].content;
    const csrfHeader = document.getElementsByName("_csrf_header")[0].content;

    try {
        const response = await fetch("/findId", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({
                name: name,
                phoneNum: phoneNum
            })
        });

        if (response.ok) {
            const data = await response.json();
            alert('아이디는 ' + data.id + ' 입니다.');
            closeFindIdPopup();
        } else {
            alert("아이디를 찾을 수 없습니다.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("서버와의 통신이 원활하지 않습니다.");
    }
}

// 아이디 찾기 버튼에 openFindIdPopup() 함수 호출 추가
document.querySelector('.login_bottom a[href="#"]').setAttribute('onclick', 'openFindIdPopup();');


function openResetPasswordPopup() {
    document.getElementById("reset-password-popup").style.display = "block";
}

function closeResetPasswordPopup() {
    document.getElementById("reset-password-popup").style.display = "none";
}

document
    .getElementById("reset-password-form")
    .addEventListener("submit", function (event) {
        event.preventDefault();

        // 폼 데이터 가져오기
        const userId = document.getElementById("resetUserId").value;
        const name = document.getElementById("resetName").value;
        const phoneNum = document.getElementById("resetPhoneNum").value;
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (newPassword !== confirmPassword) {
            alert("비밀번호가 일치하지 않습니다. 다시 입력해 주세요.");
            return false;
        }

        // AJAX 요청 전송
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/password-reset");
        xhr.setRequestHeader("Content-type", "application/json");

        xhr.onload = function () {
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    alert("비밀번호가 성공적으로 변경되었습니다.");
                    closeResetPasswordPopup();
                } else {
                    alert("비밀번호 변경에 실패했습니다.");
                }
            }
        };

        const data = {
            userId: userId,
            name: name,
            phoneNum: phoneNum,
            newPassword: newPassword,
        };
        xhr.send(JSON.stringify(data));
    });
