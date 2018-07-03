/**
 * Created by 瓦力.
 */
$(function () {
    var $city = $("#city"),
        $region = $("#region"),
        $subwayLine = $("#subwayLine"),
        $subwayStation = $("#subwayStation");

    // $city.on('change', function () {
    //     changeCity($city);
    // });

    var selectedVal = $city.val();
    changeRegion($region, selectedVal);

    // 二级联动 地区绑定
    $city.change(function () {
        // var selectedVal = $(this).val();
        // if (typeof(selectedVal) == 'undefined' || selectedVal == "") {
        //     layer.msg('请选择所在城市！', {icon: 5, time: 2000});
        //     return;
        // }

        changeRegion($region, selectedVal);
        changeSubwayLine($subwayLine, selectedVal);
    });

    // $subwayLine.on('click', function () {
    //     var city = $city.val();
    //     if (typeof(city) === 'undefined' || city === "") {
    //         layer.msg('请选择所在城市！', {icon: 5, time: 2000});
    //         return;
    //     }
    //     changeSubwayLine($subwayLine, $city.val());
    // });

    $subwayLine.change(function () {
        var selectedVal = $(this).val();
        if (typeof(selectedVal) === 'undefined' || selectedVal === "") {
            layer.msg('没选择地铁线路！', {icon: 5, time: 2000});

            return;
        }

       changeSubwayStation($subwayStation, selectedVal);
    });

    $(".uploaded-list-container a").hover(function () {
        $(this).append("<p id='pic'><img src='" + this.href + "' id='pic1'></p>");
        $(".uploaded-list-container a").mousemove(function (e) {
            $("#pic").css({
                "top": (e.pageY + 10) + "px",
                "left": (e.pageX + 20) + "px",
                "z-index": 100
            }).fadeIn("fast");
            // $("#pic").fadeIn("fast");
        });
    }, function () {
        $("#pic").remove();
    });

    $(".uploaded-list-container a").on('click', function () {
        var img = $(this),
            id = $(this).find("input").off().val(),
            target_house_id = $("#form-house-edit input[name='id']").val();
        //询问框
        layer.confirm('选择操作项', {
                btn: ['设为封面', '删除', '取消'] //按钮
            }, function () {
                $.ajax({
                    type: 'POST',
                    url: '/admin/house/cover',
                    data: {'cover_id': id, 'target_id': target_house_id},
                    success: function (data) {
                        if (data.code === 200) {
                            layer.msg('设置成功', {icon: 1, time: 1000});
                        } else {
                            layer.msg('设置失败！Reason: ' + data.message, {icon: 1, time: 3000});
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        layer.msg('设置失败！ Reason' + jqXHR.responseText, {icon: 1, time: 3000});

                    }
                });
            },
            function () {
                $.ajax({
                    type: 'DELETE',
                    url: '/admin/house/photo?id=' + id,
                    success: function (data) {
                        if (data.code === 200) {
                            img.remove();
                            layer.msg('已删除', {icon: 1, time: 1000});
                        } else {
                            layer.msg('删除失败！Reason: ' + data.message, {icon: 1, time: 3000});
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        layer.msg('删除失败！ Reason' + jqXHR.responseText, {icon: 1, time: 3000});

                    }
                });
            }, function () {
                layer.msg('已取消', {icon: 1, time: 800});

            });
        return false;
    });

    //表单验证
    $("#form-house-edit").validate({
        rules: {
            id: {
                required: true
            },
            title: {
                required: true
            },
            cityEnName: {
                required: true,
            },
            regionEnName: {
                required: true,
            },
            street: {
                required: true
            },
            detailAddress: {
                required: true
            },
            room: {
                required: true
            },
            parlour: {
                required: true
            },
            floor: {
                required: true,
                digits: true,
                min: 1
            },
            totalFloor: {
                required: true,
                digits: true,
                min: 1
            },
            buildYear: {
                required: true,
                min: 1900
            },
            area: {
                required: true,
                min: 1
            },
            price: {
                required: true,
                number: true,
                min: 1
            },
            rentWay: {
                required: true,
                min: 0,
                max: 1
            },
            direction: {
                required: true
            }
        },
        // errorElement: 'span',
        messages: {
            city: '必须指定城市',
            region: '必须指定地区'
        },
        // errorPlacement: function (error, element) { //错误信息位置设置方法
        //     error.appendTo(element); //这里的element是录入数据的对象
        // },
        onkeyup: false,
        focusCleanup: true,
        success: "valid",
        submitHandler: function (form) {
            $(form).ajaxSubmit({
                type: 'post',
                url: '/admin/house/edit', // 提交地址
                success: function (data) {
                    if (data.code === 200) {
                        alert('提交成功！');
                        parent_reload();
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.$('.btn-refresh').click();
                        parent.layer.close(index);
                        removeIframe();
                    } else {
                        layer.msg(data.message, {icon: 5, time: 2000});
                    }
                },
                error: function (request, message, e) {
                    layer.msg(request.responseText, {icon: 5, time: 2000});
                }
            });
            return false; //此处必须返回false，阻止常规的form提交
        }
    });
});