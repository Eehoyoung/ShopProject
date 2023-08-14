$(function(){ //ajax 수정필요
    $('.changestatusbtn').click(function(){
        const order_status = $(this).closest('tr').find('.omode').val();
        const id = $(this).closest('tr').find('input[type=hidden]').val();

        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            type: 'PATCH',
            url: '/market/orderList1/' + id,
            data: {status : order_status},
            beforeSend : function(xhr)
            {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            }
        }).done(function(word){
            alert(word);
            window.location.href = '/market/orderList';
        }).fail(function (error){
            alert(JSON.stringify(error));
        })
    })
})