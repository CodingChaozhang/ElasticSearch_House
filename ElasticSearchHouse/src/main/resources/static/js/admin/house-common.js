/**
 * Created by 瓦力.
 */

var tipStr = '<option value="">请选择</option>';

function showError(message) {
    layer.msg("Error: " + message, {icon: 5, time: 2000});
}

function changeCity(city) {
    $.get('/address/support/cities', function (data, status) {
        if (status !== 'success' || data.code !== 200) {
            showError(data.message);
            return;
        }
        city.html(tipStr);
        var str = '';
        $.each(data.data, function (i, item) {
            str += "<option value=" + item.en_name + ">" + item.cn_name + "</option>";
        });
        city.append(str);
    });
}

function changeRegion(region, cityName) {
    $.get('/address/support/regions?city_name=' + cityName, function (data, status) {
        if (status !== 'success' || data.code !== 200) {
            showError(data.message);
            return;
        }
        var selectedVal = region.val();
        region.html(tipStr);

        var str = "";
        $.each(data.data, function (i, item) {
            if (item.en_name === selectedVal) {
                str += "<option value=" + item.en_name + " selected='selected'>" + item.cn_name + "</option>";
            } else {
                str += "<option value=" + item.en_name + ">" + item.cn_name + "</option>";
            }
        });
        region.append(str);
    });
}

function changeSubwayLine(subwayLine, cityName) {
    $.get('/address/support/subway/line?city_name=' + cityName, function (data, status) {
        if (status !== 'success' || data.code !== 200) {
            showError(data.message);
            return;
        }
        subwayLine.html(tipStr);
        var str = "";
        $.each(data.data, function (index, subway) {
            str += "<option value=" + subway.id + ">" + subway.name + "</option>";
        });
        subwayLine.append(str);
    })
}

function changeSubwayStation(subwayStation, subwayLineId) {
    $.get('/address/support/subway/station?subway_id=' + subwayLineId, function (data, status) {
        if (status !== 'success' || data.code !== 200) {
            showError(data.message);
            return;
        }

        subwayStation.html(tipStr);
        var str = "";
        $.each(data.data, function (index, station) {
            str += "<option value=" + station.id + ">" + station.name + "</option>";
        });
        subwayStation.append(str);
    })
}