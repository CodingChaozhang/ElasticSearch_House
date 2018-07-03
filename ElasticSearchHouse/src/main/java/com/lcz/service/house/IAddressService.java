package com.lcz.service.house;



import java.util.List;
import java.util.Map;

import com.lcz.entity.SupportAddress;
import com.lcz.service.ServiceMultiResult;
import com.lcz.service.ServiceResult;
import com.lcz.service.search.BaiduMapLocation;
import com.lcz.web.dto.SubwayDTO;
import com.lcz.web.dto.SubwayStationDTO;
import com.lcz.web.dto.SupportAddressDTO;

/**
 * 地址服务接口
 * @author LvChaoZhang
 *
 */

public interface IAddressService {
	 /**
     * 获取所有支持的城市列表
     * @return
     */
    public ServiceMultiResult<SupportAddressDTO> findAllCities();
    /**
     * 根据英文简写获取具体区域的信息
     * @param cityEnName
     * @param regionEnName
     * @return
     */
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     * @param cityName
     * @return
     */
    public ServiceMultiResult findAllRegionsByCityName(String cityName);

    /**
     * 获取该城市所有的地铁线路
     * @param cityEnName
     * @return
     */
    public List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    /**
     * 获取地铁线路所有的站点
     * @param subwayId
     * @return
     */
    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId);

    /**
     * 获取地铁线信息
     * @param subwayId
     * @return
     */
    public ServiceResult<SubwayDTO> findSubway(Long subwayId);

    /**
     * 获取地铁站点信息
     * @param stationId
     * @return
     */
    public ServiceResult<SubwayStationDTO> findSubwayStation(Long stationId);

    /**
     * 根据城市英文简写获取城市详细信息
     * @param cityEnName
     * @return
     */
    public ServiceResult<SupportAddressDTO> findCity(String cityEnName);
    /**
     * 根据城市以及具体地位获取百度地图的经纬度
     */
    ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address);
    
    /**
     * 上传百度LBS数据
     */
    ServiceResult lbsUpload(BaiduMapLocation location, String title, String address,
                            long houseId, int price, int area);

    /**
     * 移除百度LBS数据
     * @param houseId
     * @return
     */
    ServiceResult removeLbs(Long houseId);
    
}
