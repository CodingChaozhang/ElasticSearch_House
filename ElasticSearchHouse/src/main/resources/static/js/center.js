/**
 * Created by 瓦力.
 */

$(function () {
    $('.xunwu-header .center-page').addClass('on');

    $('.user-left .select a').on('click', function () {
        $('.user-left .select li').each(function () {
            $(this).removeClass('hover');
        });
        $(this).parent().addClass('hover');

        var bind = "." + $(this).attr('data-bind');
        $('.right-body').each(function () {
            $(this).removeClass('on');
        });
        $(bind).addClass('on');
        selectDataSource(bind);
    });

    $('.user-right .right-body .tab span').on('click', function () {
        $(this).parent().find('span').each(function () {
            $(this).removeClass('hover');
        });
        $(this).addClass('hover');

        var bind = "." + $(this).attr('data-bind');
        $('.user-right .modify-tab .modify-content').each(function () {
            $(this).removeClass('on');
        });
        $(bind).addClass('on');
    });


    $('.user-right .wait-record button').on('click', function () {
        var selected = $('.user-right .wait-record input[name="houseId"]:checked').val();
        if (typeof (selected) === 'undefined') {
            layer.msg('请选择要预约的房源', {icon: 5, time: 2000});
            return false;
        }

        $.ajax({
            url: '/api/user/house/subscribe/date',
            type: 'POST',
            data: $('.user-right .wait-record form').serialize(),
            success: function (data) {
                if (data.code === 200) {
                    $('.user-right .wait-record input[name="houseId"]:checked').parent().remove();
                    layer.msg('预约成功', {icon: 6, time: 2000});

                } else {
                    layer.msg(data.message, {icon: 5, time: 2000})
                }
            },
            error: function (xhr, response, error) {
                layer.msg(response, {icon: 5, time: 3000});
            }
        });
        return false; // 阻止表单自动提交事件
    });

    $('.user-right .subscribe button').on('click', function () {
        var selected = $('.user-right .subscribe input[name="houseId"]:checked').val();
        if (typeof (selected) === 'undefined') {
            layer.msg('请选择要取消预约的房源', {icon: 5, time: 2000});
            return false;
        }

        layer.confirm('确认要取消预约吗', {
            btn: ['确认', '取消'] //按钮
        }, function () {
            $.ajax({
                url: '/api/user/house/subscribe?houseId=' + selected,
                type: 'DELETE',
                success: function (data) {
                    if (data.code === 200) {
                        $('.user-right .subscribe input[name="houseId"]:checked').parent().parent().remove();
                        layer.msg('已取消预约!', {icon: 6, time: 2000});
                    } else if (data.code === 403) {
                        layer.msg('请先登录,再执行操作', {icon: 5, time: 2000});
                    } else {
                        layer.msg(data.message, {icon: 5, time: 2000});
                    }
                },
                error: function (xhr, response, error) {
                    if (xhr.status === 403) {
                        layer.msg('请先登录,再执行预约操作', {icon: 5, time: 2000});
                    } else {
                        layer.msg('Server Error: ' + response, {icon: 5, time: 3000});
                    }
                }
            })
        });
    });

    loadWaitRecord();

    $('.edit-info .nameSubmit').on('click', function () {
        var name = $('.modify-tab input[name="name"]').val();
        if (typeof (name) === 'undefined' || name === null || name.length < 1) {
            layer.msg('请输入昵称', {icon: 5, time: 2000});
            return false;
        }

        $.ajax({
            url: '/api/user/info',
            data: {
                profile: 'name',
                value: name
            },
            type: 'POST',
            success: submitSuccess,
            error: submitError
        });
    });

    $('.edit-info .emailSubmit').on('click', function () {
        var email = $('.modify-tab input[name="email"]').val();
        if (typeof (email) === 'undefined' || email === null || email.length < 1) {
            layer.msg('请输入邮箱', {icon: 5, time: 2000});
            return false;
        }

        var emailPattern = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
        var n = email.search(emailPattern);
        if (n < 0) {
            layer.msg('请输入正确的邮箱', {icon: 5, time: 2000});
            return false;
        }


        $.ajax({
            url: '/api/user/info',
            data: {
                profile: 'email',
                value: email
            },
            type: 'POST',
            success: submitSuccess,
            error: submitError
        });
    });

    $('.edit-info .passwordSubmit').on('click', function () {
        var password = $('.modify-tab input[name="password"]').val();
        if (typeof (password) === 'undefined' || password === null || password.length < 1) {
            layer.msg('请输入密码', {icon: 5, time: 2000});
            return false;
        }

        $.ajax({
            url: '/api/user/info',
            data: {
                profile: 'password',
                value: password
            },
            type: 'POST',
            success: submitSuccess,
            error: submitError
        });
    });

    function submitSuccess(data) {
        if (data.code === 200) {
            layer.msg('已修改成功!', {icon: 6, time: 2000});
        } else if (data.code === 403) {
            layer.msg('登录失效!', {icon: 5, time: 2000});
        } else {
            layer.msg(data.message, {icon: 5, time: 2000});
        }
    }
    
    function submitError(xhr, response, e) {
        if (xhr.status === 403) {
            layer.msg('登录失效!', {icon: 5, time: 2000});
        } else {
            layer.msg('Server Error: ' + response, {icon: 5, time: 3000});
        }
    }

});

function selectDataSource(bind) {
    switch (bind) {
        case '.wait-record':
            loadWaitRecord();
            break;
        case '.subscribe':
            loadSubscribeList();
            break;
        case '.finish-record':
            loadFinishList();
            break;
        default:
            break;
    }
}

/**
 * 加载待看清单
 */
function loadWaitRecord() {
    layui.use('flow', function () {
        var $ = layui.jquery; //不用额外加载jQuery，flow模块本身是有依赖jQuery的，直接用即可。
        var flow = layui.flow;
        flow.load({
            elem: '#wait-record-list', //指定列表容器
            scrollElem: '.wait-record .content',
            done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
                //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                var lis = [],
                    start = (page - 1) * 3;

                $.get('/api/user/house/subscribe/list?status=1' + '&start=' + start + '&size=3',
                    function (res) {
                        layui.each(res.data, function (index, tuple) {
                            var house = tuple.first;
                            var content = '<li><input type="radio" name="houseId" value="' + house.id + '">预约此房源'
                                + '<div class="cover fl">'
                                + '<img src="http://pav17qjlw.bkt.clouddn.com/' + house.cover + '" width="100px" height="80px"></div> ' +
                                '<div class="info fl"><a><h1>' + house.title + '</h1></a><div class="des1">' +
                                '<i></i><span><a href="#" target="_blank">' + house.district + '</a></span>' +
                                '<span class="line">|</span><span>' + house.room + '室' + house.parlour + '厅</span>'
                                + '<span class="line">|</span><span>' + house.area + '平米</span></div><div class="des2">'
                                + '<i></i><span>' + house.floor + '层(共' + house.totalFloor + '层)</span><span>' +
                                house.buildYear + '建</span><span>-</span><span>' + house.street + '</span><span>' +
                                house.price + '元/月</span></div></div></li>';
                            lis.push(content);
                        });

                        next(lis.join(''), res.more);
                    });
            }
        })
    });
}

/**
 * 加载已预约列表
 */
function loadSubscribeList() {
    layui.use('flow', function () {
        var $ = layui.jquery; //不用额外加载jQuery，flow模块本身是有依赖jQuery的，直接用即可。
        var flow = layui.flow;
        flow.load({
            elem: '.subscribe .content ul', //指定列表容器
            scrollElem: '.subscribe .content',
            done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
                //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                var lis = [],
                    start = (page - 1) * 3;

                $.get('/api/user/house/subscribe/list?status=2' + '&start=' + start + '&size=3',
                    function (res) {
                        layui.each(res.data, function (index, tuple) {
                            var house = tuple.first,
                                subscribe = tuple.second;
                            var content = '<li><span' +
                                ' class="order-time">预约时间：' + (new Date(subscribe.orderTime)).Format("yyyy-MM-dd")
                                + '</span><div class="cover fl">'
                                + '<img src="http://pav17qjlw.bkt.clouddn.com/' + house.cover + '" width="100px" height="80px"></div> ' +
                                '<div class="info fl"><a><h1>' + house.title + '</h1></a><div class="des1">' +
                                '<i></i><span><a href="#" target="_blank">' + house.district + '</a></span>' +
                                '<span class="line">|</span><span>' + house.room + '室' + house.parlour + '厅</span>'
                                + '<span class="line">|</span><span>' + house.area + '平米</span></div><div class="des2">'
                                + '<i></i><span>' + house.floor + '层(共' + house.totalFloor + '层)</span><span>' +
                                house.buildYear + '建</span><span>-</span><span>' + house.street + '</span><span>' +
                                house.price + '元/月</span></div></div><div class="fl"><input type="radio" name="houseId" value="' + house.id + '">取消此预约</div></li>';
                            lis.push(content);
                        });

                        next(lis.join(''), res.more);
                    });
            }
        })
    });
}

/**
 * 加载看房记录
 */
function loadFinishList() {
    layui.use('flow', function () {
        var $ = layui.jquery; //不用额外加载jQuery，flow模块本身是有依赖jQuery的，直接用即可。
        var flow = layui.flow;
        flow.load({
            elem: '.finish-record .content ul', //指定列表容器
            scrollElem: '.finish-record .content',
            done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
                //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                var lis = [],
                    start = (page - 1) * 3;

                $.get('/api/user/house/subscribe/list?status=3' + '&start=' + start + '&size=3',
                    function (res) {
                        layui.each(res.data, function (index, tuple) {
                            var house = tuple.first,
                                subscribe = tuple.second;
                            var content = '<li><span' +
                                ' class="order-time">看房时间：' + (new Date(subscribe.orderTime)).Format("yyyy-MM-dd")
                                + '</span><div class="cover fl">'
                                + '<img src="http://pav17qjlw.bkt.clouddn.com/' + house.cover + '" width="100px" height="80px"></div> ' +
                                '<div class="info fl"><a><h1>' + house.title + '</h1></a><div class="des1">' +
                                '<i></i><span><a href="#" target="_blank">' + house.district + '</a></span>' +
                                '<span class="line">|</span><span>' + house.room + '室' + house.parlour + '厅</span>'
                                + '<span class="line">|</span><span>' + house.area + '平米</span></div><div class="des2">'
                                + '<i></i><span>' + house.floor + '层(共' + house.totalFloor + '层)</span><span>' +
                                house.buildYear + '建</span><span>-</span><span>' + house.street + '</span><span>' +
                                house.price + '元/月</span></div></div></li>';
                            lis.push(content);
                        });

                        next(lis.join(''), res.more);
                    });
            }
        })
    });
}