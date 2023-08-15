$(function () {
    $('.changestatusbtn').click(function () {
        const post_number = $(this).closest('tr').find('input[name=postNumber]').val();
        const post_company = $(this).closest('tr').find('select[name=change-postcom]').val();
        const order_status = $(this).closest('tr').find('.omode').val();
        const id = $(this).closest('tr').find('input[type=hidden]').val();

        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            type: 'PATCH',
            url: '/market/orderList1/' + id,
            data: {
                status: order_status,
                postCompany: post_company,
                postNumber: post_number
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            }
        }).done(function (word) {
            alert(word);
            window.location.href = '/market/orderList';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })
    })
})
