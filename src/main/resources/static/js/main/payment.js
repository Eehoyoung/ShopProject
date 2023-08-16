$(function () {
    let total_price = 0
    let discount = 0
    const canuse = parseInt($('.canuse').val());
    const mileage_input = 'input[name=used_mileage]';

    // 초기 총 주문금액 및 결제 예정금액 산출
    $('.item_price').each(function () {
        let order_price = parseInt($(this).val()) * parseInt($(this).siblings('.item_quantity').val());
        $(this).closest('tr').find('.order_price').html(order_price.toLocaleString()); //한 아이템에 대한 총 가격 세팅
        $(this).closest('tr').find('.order_mileage').html((order_price / 100).toLocaleString());
        total_price += order_price;
    })
    $('input[name=total_price]').val(total_price);
    $('#total').html(total_price.toLocaleString());
    $('input[name=tobepaid_price]').val(total_price)
    $('#tobepaid').html(total_price.toLocaleString());

    //체크박스 전체선택 or 해제
    $('.check_all').click(function () {
        if ($('.check_all').is(":checked")) {
            $('.check').prop('checked', true)
        } else {
            $('.check').prop('checked', false)
        }
    })

    //체크박스 선택 삭제
    $('.deletebtn').click(function () {
        $('.check').each(function () {
            if ($(this).is(':checked')) {
                let tr = $(this).closest('tr');
                total_price -= parseInt(tr.find('.item_price').val()) * parseInt(tr.find('.item_quantity').val());
                tr.remove();
                $('input[name=total_price]').val(total_price);
                $('#total').html(total_price);
                $('input[name=tobepaid_price]').val(total_price - discount);
                $('#tobepaid').html(total_price - discount);
            }
        })
        if ($('.check').length == 0) {    //상품이 전부 삭제되었을땐 메인화면으로 튕겨낸다.
            location.href = './index.html';
        }
    })

    // 마일리지 적용
    $(mileage_input).keyup(function () {
        discount = parseInt($(this).val() ? $(this).val() : 0);
        if (discount > canuse) {
            discount = canuse;
        }
        if (discount <= total_price) {
            $('#discounted').html(discount.toLocaleString());
            $('input[name=used_mileage]').val(discount);
            $('#tobepaid').html((total_price - discount).toLocaleString());
            $('input[name=tobepaid_price]').val(total_price - discount);
        }
    })

    // 배송정보 선택 AJAX
    $('input[name=address_id]').click(function () {
        console.log($(this).val());
        if ($(this).val() == 'new_addr') {
            $('#address_recipient').val('');
            $('#sample6_postcode').val('');
            $('#sample6_address').val('');
            $('#sample6_detailAddress').val('');
            $('.address_home_number:not(:first-child)').val('');
            $('.address_phone_number:not(:first-child)').val('');
        } else {

            let addr = $(this).val();
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $.ajax({
                url: "/main/payment/changeaddress/" + addr,
                type: 'POST',
                // dataType: 'json',
                beforeSend: function (xhr) {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                    xhr.setRequestHeader(header, token);
                },
                success: function (address_obj) {
                    console.log(address_obj)
                    console.log(typeof (address_obj))
                    $('#address_recipient').val(address_obj.recipient)
                    $('#sample6_postcode').val(address_obj.zipcode)
                    $('#sample6_address').val(address_obj.city)
                    $('#sample6_detailAddress').val(address_obj.street)
                    const homenumber = address_obj.addressHomePhoneNumber
                    $("#address_home_number").val(homenumber[0]).prop('selected', true)
                    $(".address_home_number").each(function (idx, item) {
                        if (idx == 0) {
                            return true
                        }
                        $(item).val(homenumber[idx])
                    })

                    const phonenumber = address_obj.addressPhoneNumber
                    $("#address_phone_number").val(phonenumber[0]).prop('selected', true)
                    $(".address_phone_number").each(function (idx, item) {
                        if (idx == 0) {
                            return true
                        }
                        $(item).val(phonenumber[idx])
                    })

                },
                error: function (e) {
                    alert(e.responseText);
                }
            })
        }
    })

})

let orderiteminfo = []


function requestPay() {
    var IMP = window.IMP; // 생략가능
    IMP.init('imp10327480');

    let item_keys = $('.item_idx').get();
    let item_colors = $('.item_color').get();
    let item_quantities = $('.item_quantity').get();
    for (let i = 0; i < item_keys.length; i++) {
        let item_obj = {}
        let key = $(item_keys[i]).val();
        let color = $(item_colors[i]).val();
        let quan = $(item_quantities[i]).val();
        item_obj = {item_idx: key, item_color: color, item_quantity: quan}
        orderiteminfo.push(item_obj);
    }


    let today = new Date();
    let hours = today.getHours(); // 시
    let minutes = today.getMinutes();  // 분
    let seconds = today.getSeconds();  // 초
    let milliseconds = today.getMilliseconds();
    let makeMerchantUid = hours + minutes + seconds + milliseconds;
    let pay = parseInt($('input[name=tobepaid_price]').val());
    let cusName = $('#address_recipient').val();
    let cusPhone = $('.address_phone_number').val();
    let cusAddr = $('#sample6_address').val();
    let cusZip = $('#sample6_postcode').val();


    // 결제 데이터
    let data = {
        pg: 'uplus',
        pay_method: 'card',
        merchant_uid: "IMP" + makeMerchantUid,
        name: 'OnlyFit',
        amount: pay,
        buyer_name: cusName,
        buyer_tel: cusPhone,
        buyer_addr: cusAddr,
        buyer_postcode: cusZip,
    };

    console.warn(pay, cusName, cusPhone, cusAddr, cusZip);

    IMP.request_pay(data, callback);
}

function callback(response) {
    if (response.success) {
        // 전송할 폼 데이터 캡처
        let formData = new FormData();
        formData.append('_csrf', document.querySelector("meta[name='_csrf']").getAttribute("content"));
        formData.append('paytype', 'card');
        formData.append('total_price', $('input[name=total_price]').val());
        formData.append('tobepaid_price', $('input[name=tobepaid_price]').val());
        formData.append('orderiteminfo', JSON.stringify(orderiteminfo));
        formData.append('address_id', $('input[name=address_id]:checked').val());
        formData.append('address_recipient', $('#address_recipient').val());
        formData.append('zipcode', $('#sample6_postcode').val());
        formData.append('city', $('#sample6_address').val());
        formData.append('street', $('#sample6_detailAddress').val());
        $('.address_home_number').each(function () {
            formData.append('addressHomePhoneNumber[]', $(this).val());
        });
        $('.address_phone_number').each(function () {
            formData.append('addressPhoneNumber[]', $(this).val());
        });
        formData.append('used_mileage', $('input[name=used_mileage]').val());

        // AJAX로 데이터 전송
        $.ajax({
            type: 'POST',
            url: '/main/payment_ok',
            data: formData,
            processData: false, // jQuery가 데이터를 가공하지 않도록 설정
            contentType: false, // contentType을 강제로 변경하지 않도록 설정
            beforeSend: function (xhr) {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(document.querySelector("meta[name='_csrf_header']").getAttribute("content"),
                    document.querySelector("meta[name='_csrf']").getAttribute("content"));
            }
        }).done(function (word) {
            $('#flag').val('true');
            alert("좋아 아주 좋아");
            location.href = "/main/order";
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });

    } else {
        let msg = '결제가 실패하였습니다.';
        msg += '\n고유ID: ' + response.imp_uid;
        msg += '\n상점 거래ID: ' + response.merchant_uid;
        msg += '\n결제 금액 : ' + response.amount;
        msg += '\n응답 메시지: ' + response.error_msg;
        alert(msg);
    }
}