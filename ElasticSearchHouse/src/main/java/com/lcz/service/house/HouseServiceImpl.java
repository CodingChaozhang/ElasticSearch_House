package com.lcz.service.house;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.lcz.base.HouseSort;
import com.lcz.base.HouseStatus;
import com.lcz.base.HouseSubscribeStatus;
import com.lcz.base.LoginUserUtil;
import com.lcz.entity.House;
import com.lcz.entity.HouseDetail;
import com.lcz.entity.HousePicture;
import com.lcz.entity.HouseSubscribe;
import com.lcz.entity.HouseTag;
import com.lcz.entity.Subway;
import com.lcz.entity.SubwayStation;
import com.lcz.repository.HouseDetailRepository;
import com.lcz.repository.HousePictureRepository;
import com.lcz.repository.HouseRepository;
import com.lcz.repository.HouseSubscribeRespository;
import com.lcz.repository.HouseTagRepository;
import com.lcz.repository.SubwayRepository;
import com.lcz.repository.SubwayStationRepository;
import com.lcz.service.ServiceMultiResult;
import com.lcz.service.ServiceResult;
import com.lcz.service.search.ISearchService;
import com.lcz.web.dto.HouseDTO;
import com.lcz.web.dto.HouseDetailDTO;
import com.lcz.web.dto.HousePictureDTO;
import com.lcz.web.dto.HouseSubscribeDTO;
import com.lcz.web.form.DatatableSearch;
import com.lcz.web.form.HouseForm;
import com.lcz.web.form.MapSearch;
import com.lcz.web.form.PhotoForm;
import com.lcz.web.form.RentSearch;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

@Service
public class HouseServiceImpl implements IHouseService{
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
    private HouseRepository houseRepository;
	@Autowired
	private HouseDetailRepository houseDetailRepository;
	@Autowired
    private HousePictureRepository housePictureRepository;
    @Autowired
    private HouseTagRepository houseTagRepository;
    @Autowired
    private SubwayRepository subwayRepository;
    @Autowired
    private SubwayStationRepository subwayStationRepository;
    @Autowired
    private HouseSubscribeRespository subscribeRespository;
    @Autowired
    private ISearchService searchService;
    @Autowired
    private IQiNiuService qiNiuService;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

	@Override
	public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidtionResult = wrapperDetailInfo(detail, houseForm);
        if (subwayValidtionResult != null) {
            return subwayValidtionResult;
        }
		
		House house = new House();
		modelMapper.map(houseForm, house);
		
		Date now=new Date();
		house.setCreateTime(now);
		house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house = houseRepository.save(house);
        
        detail.setHouseId(house.getId());
        detail = houseDetailRepository.save(detail);
        
        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.save(pictures);
        
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        
        houseDTO.setHouseDetail(houseDetailDTO);
        
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());
        
        List<String> tags = houseForm.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.save(houseTags);
            houseDTO.setTags(tags);
        }
        return new ServiceResult<HouseDTO>(true, null, houseDTO);
	}
	 /**
     * 渲染详细信息 及 标签
     * @param houseIds
     * @param idToHouseMap
     */
    private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDTO> idToHouseMap) {
        List<HouseDetail> details = houseDetailRepository.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTO.setHouseDetail(detailDTO);
        });

        List<HouseTag> houseTags = houseTagRepository.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO house = idToHouseMap.get(houseTag.getHouseId());
            house.getTags().add(houseTag.getName());
        });
    }

    /**
     * 图片对象列表信息填充
     * @param form
     * @param houseId
     * @return
     */
    private List<HousePicture> generatePictures(HouseForm form, Long houseId) {
        List<HousePicture> pictures = new ArrayList<>();
        if (form.getPhotos() == null || form.getPhotos().isEmpty()) {
            return pictures;
        }

        for (PhotoForm photoForm : form.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdnPrefix);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }

    /**
     * 房源详细信息对象填充
     * @param houseDetail
     * @param houseForm
     * @return
     */
    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());
        if (subway == null) {
            return new ServiceResult<>(false, "Not valid subway line!");
        }

        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null || subway.getId() != subwayStation.getSubwayId()) {
            return new ServiceResult<>(false, "Not valid subway station!");
        }

        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;

    }
	@Override
	public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
		List<HouseDTO> houseDTOS = new ArrayList<>();
		
		Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();

        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);
		
        Specification<House> specification = (root, query, cb) -> {
        	 Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());
        	 predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));
        	 if (searchBody.getCity() != null) {
                 predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
             }

             if (searchBody.getStatus() != null) {
                 predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
             }

             if (searchBody.getCreateTimeMin() != null) {
                 predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
             }

             if (searchBody.getCreateTimeMax() != null) {
                 predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
             }

             if (searchBody.getTitle() != null) {
                 predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
             }

             return predicate;
        };
        
        Page<House> houses = houseRepository.findAll(specification,pageable);
        
		houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);
        });
		
		return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
	}
	

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = houseDetailRepository.findByHouseId(id);
        List<HousePicture> pictures = housePictureRepository.findAllByHouseId(id);

        HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        for (HousePicture picture : pictures) {
            HousePictureDTO pictureDTO = modelMapper.map(picture, HousePictureDTO.class);
            pictureDTOS.add(pictureDTO);
        }


        List<HouseTag> tags = houseTagRepository.findAllByHouseId(id);
        List<String> tagList = new ArrayList<>();
        for (HouseTag tag : tags) {
            tagList.add(tag.getName());
        }

        HouseDTO result = modelMapper.map(house, HouseDTO.class);
        result.setHouseDetail(detailDTO);
        result.setPictures(pictureDTOS);
        result.setTags(tagList);

        if (LoginUserUtil.getLoginUserId() > 0) { // 已登录用户
            HouseSubscribe subscribe = subscribeRespository.findByHouseIdAndUserId(house.getId(), LoginUserUtil.getLoginUserId());
            if (subscribe != null) {
                result.setSubscribeStatus(subscribe.getStatus());
            }
        }
        return ServiceResult.of(result);
    }
    
    @Override
    @Transactional
    public ServiceResult update(HouseForm houseForm) {
        House house = this.houseRepository.findOne(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = this.houseDetailRepository.findByHouseId(house.getId());
        if (detail == null) {
            return ServiceResult.notFound();
        }

        ServiceResult wrapperResult = wrapperDetailInfo(detail, houseForm);
        if (wrapperResult != null) {
            return wrapperResult;
        }

        houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.save(pictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);

        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
            searchService.index(house.getId());
        }

        return ServiceResult.success();
    }
    
    @Override
    public ServiceResult removePhoto(Long id) {
        HousePicture picture = housePictureRepository.findOne(id);
        if (picture == null) {
            return ServiceResult.notFound();
        }

        try {
            Response response = this.qiNiuService.delete(picture.getPath());
            if (response.isOK()) {
                housePictureRepository.delete(id);
                return ServiceResult.success();
            } else {
                return new ServiceResult(false, response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult updateCover(Long coverId, Long targetId) {
        HousePicture cover = housePictureRepository.findOne(coverId);
        if (cover == null) {
            return ServiceResult.notFound();
        }

        houseRepository.updateCover(targetId, cover.getPath());
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ServiceResult(false, "标签已存在");
        }

        houseTagRepository.save(new HouseTag(houseId, tag));
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
        if (houseTag == null) {
            return new ServiceResult(false, "标签不存在");
        }

        houseTagRepository.delete(houseTag.getId());
        return ServiceResult.success();
    }
    
    @Override
    @Transactional
    public ServiceResult updateStatus(Long id, int status) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }

        if (house.getStatus() == status) {
            return new ServiceResult(false, "状态没有发生变化");
        }

        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return new ServiceResult(false, "已出租的房源不允许修改状态");
        }

        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return new ServiceResult(false, "已删除的资源不允许操作");
        }

        houseRepository.updateStatus(id, status);

        // 上架更新索引 其他情况都要删除索引
        if (status == HouseStatus.PASSES.getValue()) {
            searchService.index(id);
        } else {
            searchService.remove(id);
        }
        return ServiceResult.success();
    }
    
    private List<HouseDTO> wrapperHouseResult(List<Long> houseIds) {
        List<HouseDTO> result = new ArrayList<>();

        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            idToHouseMap.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseMap);

        // 矫正顺序
        for (Long houseId : houseIds) {
            result.add(idToHouseMap.get(houseId));
        }
        return result;
    }
    
    @Override
    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
    	if (rentSearch.getKeywords() != null && !rentSearch.getKeywords().isEmpty()) {
            ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
            if (serviceResult.getTotal() == 0) {
                return new ServiceMultiResult<>(0, new ArrayList<>());
            }

            return new ServiceMultiResult<>(serviceResult.getTotal(), wrapperHouseResult(serviceResult.getResult()));
        }
        return simpleQuery(rentSearch);
        
    }
    private ServiceMultiResult<HouseDTO> simpleQuery(RentSearch rentSearch) {
        Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        int page = rentSearch.getStart() / rentSearch.getSize();

        Pageable pageable = new PageRequest(page, rentSearch.getSize(), sort);

        Specification<House> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSES.getValue());

            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cityEnName"), rentSearch.getCityEnName()));

            if (HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearch.getOrderBy())) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY), -1));
            }
            return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification, pageable);
        List<HouseDTO> houseDTOS = new ArrayList<>();


        List<Long> houseIds = new ArrayList<>();
        Map<Long, HouseDTO> idToHouseMap = Maps.newHashMap();
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseMap.put(house.getId(), houseDTO);
        });


        wrapperHouseList(houseIds, idToHouseMap);
        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }
    
    @Override
    public ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> serviceResult = searchService.mapQuery(mapSearch.getCityEnName(), mapSearch.getOrderBy(), mapSearch.getOrderDirection(), mapSearch.getStart(), mapSearch.getSize());

        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResult<>(0, new ArrayList<>());
        }
        List<HouseDTO> houses = wrapperHouseResult(serviceResult.getResult());
        return new ServiceMultiResult<>(serviceResult.getTotal(), houses);
    }
    @Override
    public ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> serviceResult = searchService.mapQuery(mapSearch);
        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResult<>(0, new ArrayList<>());
        }

        List<HouseDTO> houses = wrapperHouseResult(serviceResult.getResult());
        return new ServiceMultiResult<>(serviceResult.getTotal(), houses);
    }

    @Override
    @Transactional
    public ServiceResult addSubscribeOrder(Long houseId) {
        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = subscribeRespository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe != null) {
            return new ServiceResult(false, "已加入预约");
        }

        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return new ServiceResult(false, "查无此房");
        }

        subscribe = new HouseSubscribe();
        Date now = new Date();
        subscribe.setCreateTime(now);
        subscribe.setLastUpdateTime(now);
        subscribe.setUserId(userId);
        subscribe.setHouseId(houseId);
        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_LIST.getValue());
        subscribe.setAdminId(house.getAdminId());
        subscribeRespository.save(subscribe);
        return ServiceResult.success();
    }
    
    @Override
    public ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(
            HouseSubscribeStatus status,
            int start,
            int size) {
        Long userId = LoginUserUtil.getLoginUserId();
        Pageable pageable = new PageRequest(start / size, size, new Sort(Sort.Direction.DESC, "createTime"));

        Page<HouseSubscribe> page = subscribeRespository.findAllByUserIdAndStatus(userId, status.getValue(), pageable);

        return wrapper(page);
    }
    private ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> wrapper(Page<HouseSubscribe> page) {
        List<Pair<HouseDTO, HouseSubscribeDTO>> result = new ArrayList<>();

        if (page.getSize() < 1) {
            return new ServiceMultiResult<>(page.getTotalElements(), result);
        }

        List<HouseSubscribeDTO> subscribeDTOS = new ArrayList<>();
        List<Long> houseIds = new ArrayList<>();
        page.forEach(houseSubscribe -> {
            subscribeDTOS.add(modelMapper.map(houseSubscribe, HouseSubscribeDTO.class));
            houseIds.add(houseSubscribe.getHouseId());
        });

        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house -> {
            idToHouseMap.put(house.getId(), modelMapper.map(house, HouseDTO.class));
        });

        for (HouseSubscribeDTO subscribeDTO : subscribeDTOS) {
            Pair<HouseDTO, HouseSubscribeDTO> pair = Pair.of(idToHouseMap.get(subscribeDTO.getHouseId()), subscribeDTO);
            result.add(pair);
        }

        return new ServiceMultiResult<>(page.getTotalElements(), result);
    }
    
    @Override
    @Transactional
    public ServiceResult subscribe(Long houseId, Date orderTime, String telephone, String desc) {
        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = subscribeRespository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return new ServiceResult(false, "无预约记录");
        }

        if (subscribe.getStatus() != HouseSubscribeStatus.IN_ORDER_LIST.getValue()) {
            return new ServiceResult(false, "无法预约");
        }

        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_TIME.getValue());
        subscribe.setLastUpdateTime(new Date());
        subscribe.setTelephone(telephone);
        subscribe.setDesc(desc);
        subscribe.setOrderTime(orderTime);
        subscribeRespository.save(subscribe);
        return ServiceResult.success();
    }
    @Override
    @Transactional
    public ServiceResult cancelSubscribe(Long houseId) {
        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = subscribeRespository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return new ServiceResult(false, "无预约记录");
        }

        subscribeRespository.delete(subscribe.getId());
        return ServiceResult.success();
    }
    @Override
    public ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size) {
        Long userId = LoginUserUtil.getLoginUserId();
        Pageable pageable = new PageRequest(start / size, size, new Sort(Sort.Direction.DESC, "orderTime"));

        Page<HouseSubscribe> page = subscribeRespository.findAllByAdminIdAndStatus(userId, HouseSubscribeStatus.IN_ORDER_TIME.getValue(), pageable);

        return wrapper(page);
    }
    
    @Override
    @Transactional
    public ServiceResult finishSubscribe(Long houseId) {
        Long adminId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = subscribeRespository.findByHouseIdAndAdminId(houseId, adminId);
        if (subscribe == null) {
            return new ServiceResult(false, "无预约记录");
        }

        subscribeRespository.updateStatus(subscribe.getId(), HouseSubscribeStatus.FINISH.getValue());
        houseRepository.updateWatchTimes(houseId);
        return ServiceResult.success();
    }

}
