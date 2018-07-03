/**
 * Created by 瓦力.
 */

Date.prototype.Format = function(fmt) {

    var o = {
        "M+": this.getMonth() + 1,
        //月份

        "d+": this.getDate(),
        //日

        "h+": this.getHours(),
        //小时

        "m+": this.getMinutes(),
        //分

        "s+": this.getSeconds(),
        //秒

        "q+": Math.floor((this.getMonth() + 3) / 3),
        //季度

        "S": this.getMilliseconds() //毫秒

    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
};

function parent_reload() {
    window.parent.location.reload();
}

function mySelfInfo(userId) {
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