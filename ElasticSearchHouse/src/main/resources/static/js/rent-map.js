/**
 * Created by codingchaozhang.
 */
var regionCountMap = {}, // 地区数据
    labels = [], // 标签列表
    params = {
        orderBy: 'lastUpdateTime',
        orderDirection: 'desc'
    },
    customLayer; // 麻点图

function load(city, regions, aggData) {
    // 百度地图API功能
    var map = new BMap.Map("allmap", {minZoom: 12}); // 创建实例。设置地图显示最大级别为城市
    var point = new BMap.Point(city.baiduMapLongitude, city.baiduMapLatitude); // 城市中心
    map.centerAndZoom(point, 12); // 初始化地图，设置中心点坐标及地图级别

    map.addControl(new BMap.NavigationControl({enableGeolocation: true})); // 添加比例尺控件
    map.addControl(new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT})); // 左上角
    map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放

    for (var i = 0; i < aggData.length; i++) {
        regionCountMap[aggData[i].key] = aggData[i].count;
    }

    drawRegion(map, regions);

    loadHouseData();

    // 缩放事件
    map.addEventListener('zoomend', function (event) {
        mapResize(event.target);
    });
    // 地图拖拽完成事件
    map.addEventListener('dragend', function (event) {
        mapResize(event.target);
    });
}

/**
 * 刻画地区
 * @param map
 * @param regionList
 */
function drawRegion(map, regionList) {
    var boundary = new BMap.Boundary();
    var polygonContext = {};
    var regionPoint;
    var textLabel;
    for (var i = 0; i < regionList.length; i++) {

        regionPoint = new BMap.Point(regionList[i].baiduMapLongitude, regionList[i].baiduMapLatitude);

        var houseCount = 0;
        if (regionList[i].en_name in regionCountMap) {
            houseCount = regionCountMap[regionList[i].en_name];
        }

        var textContent = '<p style="margin-top: 20px; pointer-events: none">' +
            regionList[i].cn_name + '</p>' + '<p style="pointer-events: none">' +
            houseCount + '套</p>';
        textLabel = new BMap.Label(textContent, {
            position: regionPoint, // 标签位置
            offset: new BMap.Size(-40, 20) // 文本偏移量
        });

        textLabel.setStyle({
            height: '78px',
            width: '78px',
            color: '#fff',
            backgroundColor: '#0054a5',
            border: '0px solid rgb(255, 0, 0)',
            borderRadius: "50%",
            fontWeight: 'bold',
            display: 'inline',
            lineHeight: 'normal',
            textAlign: 'center',
            opacity: '0.8',
            zIndex: 2,
            overflow: 'hidden'
        });

        map.addOverlay(textLabel); // 将标签画在地图上
        labels.push(textLabel);

        // 记录行政区域覆盖物
        polygonContext[textContent] = []; // 点集合
        (function (textContent) { // 闭包传参
            boundary.get(city.cn_name + regionList[i].cn_name, function(rs) { // 获取行政区域
                var count = rs.boundaries.length; // 行政区域边界点集合长度
                if (count === 0) {
                    alert('未能获取当前输入行政区域')
                    return;
                }

                for (var j = 0; j < count; j++) {
                    // 建立多边形覆盖物
                    var polygon = new BMap.Polygon(
                        rs.boundaries[j],
                        {
                            strokeWeight: 2,
                            strokeColor:'#0054a5',
                            fillOpacity: 0.3,
                            fillColor: '#0054a5'
                        }
                    );
                    map.addOverlay(polygon); // 添加覆盖物
                    polygonContext[textContent].push(polygon);
                    polygon.hide(); // 初始化隐藏边界
                }
            })
        })(textContent);

        textLabel.addEventListener('mouseover', function (event) {
            var label = event.target;
            var boundaries = polygonContext[label.getContent()];

            label.setStyle({backgroundColor: '#1AA591'});
            for (var n = 0; n < boundaries.length; n++) {
                boundaries[n].show();
            }
        });

        textLabel.addEventListener('mouseout', function (event) {
            var label = event.target;
            var boundaries = polygonContext[label.getContent()];

            label.setStyle({backgroundColor: '#0054a5'});
            for (var n = 0; n < boundaries.length; n++) {
                boundaries[n].hide();
            }
        });

        textLabel.addEventListener('click', function (event) {
            var label = event.target;
            var map = label.getMap();
            map.zoomIn();
            map.panTo(event.point);
        });
    }

    if (!customLayer) {
        customLayer = new BMap.CustomLayer({
            geotableId: 191291,
            q: '', // 检索关键字
            tags: '', // 空格分隔的字符串
            filter: '', // 过滤条件，参考：http://lbsyun.baidu.com/index.php?title=lbscloud/api/geosearch
            pointDensityType: BMAP_POINT_DENSITY_HIGH
        });
        map.addTileLayer(customLayer); // 添加自定义图层
        customLayer.addEventListener('onhotspotclick', houseTip); // 单击图层事件
    }

    function houseTip(e) {
        var customPoi = e.customPoi; // poi的默认字段
        var contentPoi = e.content; // poi的自定义字段

        var content =  '<p style="width:280px;margin: 0; line-height: 20px;">地址：' +
            customPoi.address + '<br/>价格：' + contentPoi.price + '元/月<br/>面积：'
            + contentPoi.area + '平方米</p>';
        var opts = {
            width: 200, // 信息窗口宽度
            height: 100, // 信息窗口高度
            title: contentPoi.title, // 信息窗口标题
            enableMessage: true, // 设置允许信息窗发送信息
            message: contentPoi.address
        };

        // 创建信息窗口对象
        var infoWindow = new BMap.InfoWindow("位置：" + contentPoi.address, opts);
        var point = new BMap.Point(customPoi.point.lng, customPoi.point.lat);

        // 搜索信息提示框
        var searchInfoWindow = new BMapLib.SearchInfoWindow(map, content, {
           title: customPoi.title, // 标题
            width: 290,
            height: 60,
            panel: "panel", // 搜索结果面板
            enableAutoPan: false, // 关闭自动平移 防止触发mapResize
            enableSendToPhone: true, // 是否显示发送到手机按钮
            searchTypes: [
                BMAPLIB_TAB_SEARCH, // 周边检索
                // BMAPLIB_TAB_TO_HERE, // 到这里去
                // BMAPLIB_TAB_FROM_HERE // 从这里出发
            ]
        });

        var marker = new BMap.Marker(point); // 创建marker标注
        marker.addEventListener("click", function (e) { // 点击去除
            map.removeOverlay(e.target);
        });

        marker.setAnimation(BMAP_ANIMATION_BOUNCE); // 跳动的动画
        searchInfoWindow.open(marker);
        map.addOverlay(marker);
    }
}

function mapResize(_map) {
    var bounds = _map.getBounds(),
        southWest = bounds.getSouthWest(), // 西南角
        northEast = bounds.getNorthEast(); // 东北角

    var zoomLevel = _map.getZoom();

    params = {
        level: zoomLevel,
        leftLongitude: southWest.lng, // 左上角
        leftLatitude: northEast.lat,
        rightLongitude: northEast.lng, // 右下角
        rightLatitude: southWest.lat
    };

    if (zoomLevel < 13) {
        for (var i = 0; i < labels.length; i++) {
            labels[i].show();
        }
    } else {
        loadHouseData();
        for (var i = 0; i < labels.length; i++) {
            labels[i].hide();
        }
    }
}

/**
 * 加载房源数据 并且进行渲染
 */
function loadHouseData() {
    var target = '&'; // 拼接参数
    $.each(params, function (key, value) {
        target += (key + '=' + value + '&');
    });

    $('#house-flow').html('');
    layui.use('flow', function () {
        var $ = layui.jquery; //不用额外加载jQuery，flow模块本身是有依赖jQuery的，直接用即可。
        var flow = layui.flow;
        flow.load({
            elem: '#house-flow', //指定列表容器
            scrollElem: '#house-flow',
            done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
                //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                var lis = [],
                    start = (page - 1) * 3;

                var cityName = $('#cityEnName').val();
                $.get('/rent/house/map/houses?cityEnName=' + cityName + '&start=' + start + '&size=3' + target,
                    function (res) {
                        if (res.code !== 200) {
                            lis.push('<li>数据加载错误</li>');
                        } else {
                            layui.each(res.data, function (index, house) {
                                var direction;
                                switch (house.direction) {
                                    case 1:
                                        direction = '朝东';
                                        break;
                                    case 2:
                                        direction = '朝南';
                                        break;
                                    case 3:
                                        direction = '朝西';
                                        break;
                                    case 4:
                                        direction = '朝北';
                                        break;
                                    case 5:
                                    default:
                                        direction = '南北';
                                        break;
                                };

                                var tags = '';
                                for (var i = 0; i < house.tags.length; i++) {
                                    tags += '<span class="item-tag-color_2 item-extra">' + house.tags[i] + '</span>';
                                }
                                var li = '<li class="list-item"><a href="/rent/house/show/' + house.id + '" target="_blank"'
                                    + ' title="' + house.title + '"data-community="1111027382235"> <div class="item-aside">'
                                    + '<img src="' + house.cover + '?imageView2/1/w/116/h/116"><div class="item-btm">'
                                    + '<span class="item-img-icon"><i class="i-icon-arrow"></i><i class="i-icon-dot"></i>'
                                    + '</span>&nbsp;&nbsp;</div></div><div class="item-main"><p class="item-tle">'
                                    + house.title + '</p><p class="item-des"> <span>' + house.room + '室' + house.parlour + '厅'
                                    + '</span><span>' + house.area + '平米</span> <span>' + direction + '</span>'
                                    + '<span class="item-side">' + house.price + '<span>元/月</span></span></p>'
                                    + '<p class="item-community"><span class="item-replace-com">' + house.district + '</span>'
                                    + '<span class="item-exact-com">' + house.district + '</span></p><p class="item-tag-wrap">'
                                    + tags + '</p></div></a></li>';

                                lis.push(li);
                            });
                        }

                        //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                        //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                        next(lis.join(''), res.more);
                    });
            }
        });
    });
}

// 排序切换
$('ol.order-select li').on('click', function () {
    $('ol.order-select li.on').removeClass('on');
    $(this).addClass('on');
    params.orderBy = $(this).attr('data-bind');
    params.orderDirection = $(this).attr('data-direction');
    loadHouseData();
});
