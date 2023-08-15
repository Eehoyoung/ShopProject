$(function () {
    //side-bar toggle
    $('.menu-dropdown').click(function () {
        $(this).children('.dropdown').slideToggle();
    })
    $('.dropdown').click(function (e) {
        e.stopPropagation();
    })
});

$(document).ready(function () {
    $("#homeButton").click(function (event) { // homeButton ID가 지정된 요소 클릭 시 실행되는 함수입니다.
        event.preventDefault();

        $.ajax({
            url: "/getMarketId",
            type: "GET",
            success: function (marketid) {
                window.location.href = "/market/main/" + marketid; // 받아온 marketID 값으로 URL 변경 후 페이지 이동
            },
            error: function (request, status, error) {
                alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
            }
        });
    });
});
