/**
 * Created by 瓦力.
 */
// 【关键】数据显示控制 服务器分页
var table = $('#data-table').DataTable({
    "order": [[7, "desc"]],//默认创建时间排序
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
        data: "id",
    }, {
        data: "title",
        searchable: false // 通过全局搜索框搜索标题
    }, {
        data: "cover",
        searchable: false,
        orderable: false
    }, {
        data: "area",
    }, {
        data: "price",
    }, {
        data: "floor",
    }, {
        data: "watchTimes",
    }, {
        data: "createTime"
    }, {
        data: "status",
        orderable: false
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
                'onClick="house_edit(\'查看\', \'/admin/house/show?id=' + row.id + '\')" title="查看">' + data + '</u></td>';
        }
    }, {
        targets: 2,
        render: function (data, type, row, meta) {
            return '<td><img onClick="house_edit(\'查看\', \'/admin/house/show?id=' + row.id + '\')" title="查看"' +
                ' class="picture-thumb" src="' + data + '?imageView2/1/w/200/h/100"></td>';
        }
    }, {
        targets: 7,
        render: function (data, type, row, meta) {
            return (new Date(data)).Format("yyyy-MM-dd hh:mm:ss");
        }
    }, {
        targets: 8,
        render: function (data, type, row, meta) {
            var html = '';
            if (data === 0) {
                html = '<td class="td-status"><span class="label label-danger radius">待审核</span></td>';
            } else if (data === 1) {
                html = '<td class="td-status"><span class="label label-success radius">已发布</span></td>';
            } else if (data === 2) {
                html = '<td class="td-status"><span class="label label-warning radius">已出租</span></td>';
            } else {
                html = '<td class="td-status"><span class="label label-danger radius">未知状态</span></td>';
            }
            return html;
        }
    }, {
        targets: 9,
        render: function (data, type, row, meta) {
            var prefix = '<td class="f-14 td-manage">',
                data_status = row.status,
                content = '',
                suffix = '<a style="text-decoration:none" class="ml-5"' +
                    ' onClick="house_edit(\'房源编辑\', \'/admin/house/edit?id=' + row.id + '\')" href="javascript:;"' +
                    ' title="编辑"><i class="Hui-iconfont">&#xe6df;</i></a> <a style="text-decoration:none" class="ml-5"' +
                    ' onClick="house_del(this, ' + row.id + ')" href="javascript:;" title="删除"><i' +
                    ' class="Hui-iconfont">&#xe6e2;</i></a></td>';
            if (data_status === 0) { // 待审核
                content = '<a style="text-decoration:none" onClick="house_pass(this,' + row.id + ')"' +
                    ' href="javascript:;" title="发布">发布</a>&nbsp;';
            } else if (data_status === 1) { // 已发布
                content = '<a style="text-decoration:none" onClick="house_stop(this,' + row.id + ')"' +
                    ' href="javascript:;" title="下架"><i class="Hui-iconfont">&#xe6de;</i></a>&nbsp;'
            }

            return prefix + content + suffix;
        }
    }],
    ajax: {
        type: "POST",
        url: "/admin/houses", // 服务器url
        cache: false,
        data: function (data) {
            var postData = {},
             houseStatus = $('#houseStatus').val(),
                createTimeMin = $('#createTimeMin').val(),
                createTimeMax = $('#createTimeMax').val(),
                city = $('#city').val(),
                title = $('#houseTitle').val();
            if (houseStatus.length > 0) {
                postData.status = houseStatus;
            }
            if (city.length > 0) {
                postData.city = city;
            }
            if (title.length > 0) {
                postData.title = title;
            }

            var orderColumn = data.columns[data.order[0].column];
            postData.direction = data.order[0].dir;
            postData.orderBy = orderColumn.data;

            postData.createTimeMin = createTimeMin;
            postData.createTimeMax = createTimeMax;
            postData.draw = data.draw;
            postData.start = data.start;
            postData.length = data.length;
            return postData;
        }

    }
});

$(function () {
    var $city = $("#city");

    // 城市绑定
    $.get('/address/support/cities', function (data, status) {
        if (status !== 'success' || data.code !== 200) {
            alert("Error: " + data.message);
            return;
        }
        var str = '';
        $.each(data.data, function (i, item) {
            str += "<option value=" + item.en_name + ">" + item.cn_name + "</option>";
        });
        $city.append(str);
    });

});

$('#houseSearch').on('click', null, function () {
    reloadTable();
});

function reloadTable() {
    table.ajax.reload(null, false);
}

/* 查看或编辑房源*/
function house_edit(title, url) {
    var index = layer.open({
        type: 2,
        title: title,
        content: url
    });
    layer.full(index);
}

function house_del(obj, id) {
    layer.confirm('确认要删除吗？', function (index) {
        $.ajax({
            type: 'PUT',
            url: '/admin/house/operate/' + id + '/' + '3',
            success: function (data) {
                if (data.code === 200) {
                    $(obj).parents("tr").remove();
                    layer.msg('已删除!', {icon: 1, time: 1000});
                    reloadTable();
                } else {
                    layer.msg('删除失败！' + data.message, {icon: 5, time: 1000});

                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                layer.msg('删除失败！' + jqXHR.responseText, {icon: 5, time: 3000});
            }
        });
    });
}

/*房源-下架*/
function house_stop(obj, id) {
    layer.confirm('确认要下架吗？', function (index) {
        $.ajax({
            type: 'PUT',
            url: '/admin/house/operate/' + id + '/' + '2',
            success: function (data) {
                if (data.code === 200) {
                    $(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none"' +
                        ' onClick="house_pass(this,id)" href="javascript:;" title="发布"><i' +
                        ' class="Hui-iconfont">&#xe603;</i></a>');
                    $(obj).parents("tr").find(".td-status").html('<span class="label label-defaunt radius">已下架</span>');
                    $(obj).remove();
                    layer.msg('已下架!', {icon: 5, time: 1000});
                    reloadTable();
                } else {
                    layer.msg('下架失败！' + data.message, {icon: 5, time: 1000});
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                layer.msg('下架失败！' + jqXHR.responseText, {icon: 5, time: 3000});
            }
        });
    });
}

/*房源-发布*/
function house_pass(obj, id) {
    layer.confirm('确认要发布吗？', function (index) {
        $.ajax({
            type: 'PUT',
            url: '/admin/house/operate/' + id + '/' + '1',
            success: function (data) {
                if (data.code === 200) {
                    $(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none"' +
                        ' onClick="house_stop(this,id)" href="javascript:;" title="下架"><i' +
                        ' class="Hui-iconfont">&#xe6de;</i></a>');
                    $(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
                    $(obj).remove();
                    layer.msg('已发布!', {icon: 6, time: 1000});
                    reloadTable();
                } else {
                    layer.msg('发布失败！' + data.message, {icon: 5, time: 1000});
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                layer.msg('发布失败！' + jqXHR.responseText, {icon: 5, time: 3000});
            }
        });


    });
}
