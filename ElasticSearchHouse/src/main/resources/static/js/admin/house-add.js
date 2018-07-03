/**
 * Created by 瓦力.
 */

$(function () {
    // 自定义地址选择器
    var $city = $("#city"),
        $region = $("#region"),
        $subwayLine = $("#subwayLine"),
        $subwayStation = $("#subwayStation");

    changeCity($city);

    // 二级联动 地区 以及 地铁线路 动态变动
    $city.change(function () {
        var selectedVal = $(this).val();
        if (typeof(selectedVal) == 'undefined' || selectedVal == "") {
            layer.msg('请选择所在城市！', {icon: 5, time: 2000});
            return;
        }

        changeRegion($region, selectedVal);
        changeSubwayLine($subwayLine, selectedVal);
    });

    // 地铁站三级联动
    $subwayLine.change(function () {
        var selectedVal = $(this).val();
        if (typeof(selectedVal) == 'undefined' || selectedVal == "") {
            layer.msg('请选择地铁线路！', {icon: 5, time: 2000});
            return;
        }

        changeSubwayStation($subwayStation, selectedVal);
    });

    var tags = new Set();
    $('#tags span').on('click', function () {
       var tag = $(this).text();
       if (tags.has(tag)) {
           $(this).removeClass('label-success').addClass('label-default').css('border', 'none');
           tags.delete(tag);
       } else {
           $(this).removeClass('label-default').addClass('label-success').css('border', 'solid black 1px');
           tags.add(tag);
       }
    });

    //表单验证
    $("#form-house-add").validate({
        rules: {
            title: {
                required: true,
                maxlength: 30
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
            address: {
                required: true,
                maxlength: 30
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
        messages: { // 自定义提示信息
            province: '必须指定省份',
            city: '必须指定城市',
            region: '必须指定地区'
        },
        errorPlacement: function (error, element) { //错误信息位置设置方法
            error.appendTo(element); //这里的element是录入数据的对象
        },
        onkeyup: false,
        focusCleanup: true,
        success: "valid",
        submitHandler: function (form) {
            var cover = $(form).find("input:radio[name='cover']:checked").val();

            if (cover == null || typeof(cover) == "undefined" || cover == "" || cover.length < 1) {
                layer.msg('至少要上传一个封面！', {icon: 5, time: 2000});
                return false;
            }

            $(form).find('input.house-tag').remove();
            var index = 0;
            tags.forEach(function (tag) {
               $(form).append('<input class="house-tag" name="tags[' + index++ + ']" type="hidden" value="'+ tag + '"/>');
            });

            $(form).ajaxSubmit({
                type: 'post',
                url: '/admin/add/house', // 提交地址
                success: function (data) {
                    if (data.code === 200) {
                        alert('提交成功！');
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
