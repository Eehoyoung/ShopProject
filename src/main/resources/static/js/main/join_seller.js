function joinSellerSendit() {
    let name = $('#store_name').val();
    if (name.length < 3 || name.length > 10) {
        alert("매장명은 3자 이상 10자 이하로 입력해주세요.");
        $('#store_name').focus();
        return false;
    }

    let businessNumberRegEx = /^\d{3}-\d{2}-\d{5}$/;
    let businessNumber = $('#business_number').val() + '-' + $('.business_number:eq(0)').val() + '-'
        + $('.business_number:eq(1)').val();
    if (!businessNumberRegEx.test(businessNumber)) {
        alert("사업자 번호는 000-00-00000 형식으로 입력되어야 합니다.");
        return false;
    }

    let isempty = false;
    $('.phone_number').each(function () {
        if ($(this).val() === "") {
            alert("매장 전화 항목은 필수 입력값입니다.");
            $(this).focus();
            isempty = true;
            return false;
        }
    });

    if (isempty) {
        return false;
    }
}

$(function () {

    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    $('#double_check_name').on('click', function () {
        $.ajax({
            type: 'POST',
            url: '/main/join/seller/doublecheck',
            data: {name: $('#store_name').val()},
            beforeSend: function (xhr) {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            }
        }).done(function (word) {
            $('#flag').val('true');
            alert(word);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })
    })
})
