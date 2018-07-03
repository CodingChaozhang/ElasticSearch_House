/**
 * Created by 瓦力.
 */
// 【关键】数据显示控制 服务器分页
var table = $('#data-table').DataTable({
        "pageLength": 3, // 配置单页显示条数
        "paging": true, // 关闭本地分页
        "lengthChange": false, // 不允许用户改变表格每页显示的记录数
        "searching": false, // 不允许Datatables开启本地搜索
        "ordering": true, // 启用Datatables排序
        "info": true, // 表格左边显示搜索信息
        "autoWidth": true, // 自动计算表格宽度
        "stateSave": false, // 允许表格缓存Datatables，以便下次恢复之前的状态
        "retrieve": true, // 如果已经初始化了，则继续使用之前的Datatables实例
        "processing": true, // 显示正在处理的状态
        "serverSide": true, // 服务器模式，数据由服务器掌控
        "pagingType": "simple_numbers", // 翻页显示: 上一页和下一页两个按钮，加上页数按钮
        "language": {
            "sProcessing": "处理中...",
            "sLengthMenu": "显示 _MENU_ 项结果",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
            "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix": "",
            "sUrl": "",
            "sEmptyTable": "未搜索到数据",
            "sLoadingRecords": "载入中...",
            "sInfoThousands": ",",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上页",
                "sNext": "下页",
                "sLast": "末页"
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列",
                "sSortDescending": ": 以降序排列此列"
            }
        },
        columns: [{ // 绑定数据源
            data: "first.id",
        }, {
            data: "first.title",
            searchable: false // 通过全局搜索框搜索标题
        }, {
            data: "first.cover",
            searchable: false,
            orderable: false
        }, {
            data: "first.area"
        }, {
            data: "first.price"
        }, {
            data: "first.floor"
        }, {
            data: "first.watchTimes"
        }, {
            data: "second.orderTime"
        }, {
            data: null
        }],
        columnDefs: [{ // 定义表格样式
            targets: 0,
            render: function (data, type, row, meta) {
                return '<td class="text-l"><u style="cursor:pointer" class="text-primary"' +
                    'onClick="house_edit(\'查看\', \'/admin/house/show?id=' + data + '\')" title="查看">' + data + '</u></td>';
            }
        }, {
            targets: 1,
            render: function (data, type, row, meta) {
                return '<td class="text-l"><u style="cursor:pointer" class="text-primary"' +
                    'onClick="house_edit(\'查看\', \'/house/show?id=' + row.first.id + '\')" title="查看">' + data + '</u></td>';
            }
        }, {
            targets: 2,
            render: function (data, type, row, meta) {
                return '<td><img onClick="house_edit(\'查看\', \'/house/show?id=' + row.first.id + '\')" title="查看"' +
                    ' class="picture-thumb" src="http://pav17qjlw.bkt.clouddn.com/' + data + '?imageView2/1/w/200/h/100"></td>';
            }
        }, {
            targets: 7,
            render: function (data, type, row, meta) {
                return (new Date(data)).Format("yyyy-MM-dd");
            }
        }, {
            targets: 8,
            render: function (data, type, row, meta) {
                return '<a style="text-decoration: underline" onclick="userTip(' + row.second.userId + ')">查看用户信息</a>'
            }
        }, {
            targets: 9,
            render: function (data, type, row, meta) {
                return '<td class="f-14 td-manage"><a style="text-decoration:none" class="ml-5"' +
                    ' onClick="finishSubscribe(this,' + row.first.id + ')" href="javascript:;"' +
                    ' title="带看完成"><i class="Hui-iconfont">&#xe603;</i></a></td>';
            }
        }],
        ajax: {
            type: "GET",
            url: "/admin/house/subscribe/list", // 服务器url
            cache: false,
            data: function (data) {
                var params = {};
                params.draw = data.draw;
                params.start = data.start;
                params.length = data.length;
                return params;
            }

        }
    });

function reloadTable() {
    table.ajax.reload(null, false);
}

function userTip(userId) {
    $.get('/admin/user/' + userId, function (res) {
        layer.open({
            type: 1,
            title: false, //不显示标题栏
            closeBtn: false,
            area: '300px;',
            shade: 0.8,
            id: 'LAY_layuipro', //设定一个id，防止重复弹出
            btn: ['Close'],
            moveType: 1, //拖拽模式，0或者1
            content: '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff;' +
            ' font-weight: 300;">用户名：' + res.data.name + '<br>联系电话：' + res.data.phoneNumber + '</div>'
        });
    });
}

function finishSubscribe(obj, id) {
    layer.confirm('确认完成客户带看了吗？', function () {
        $.ajax({
            type: 'POST',
            url: '/admin/finish/subscribe',
            data: {
                house_id: id
            },
            success: function (data) {
                if (data.code === 200) {
                    $(obj).parents("tr").remove();
                    layer.msg('已完成!', {icon: 1, time: 1000});
                    reloadTable();
                } else {
                    layer.msg('状态修改失败！' + data.message, {icon: 5, time: 1000});

                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                layer.msg('状态修改失败！' + jqXHR.responseText, {icon: 5, time: 3000});
            }
        });
    });
}