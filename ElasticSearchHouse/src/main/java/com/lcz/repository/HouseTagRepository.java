package com.lcz.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.lcz.entity.HouseTag;

public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {
	 List<HouseTag> findAllByHouseId(Long id);

	 List<HouseTag> findAllByHouseIdIn(List<Long> houseIds);

	HouseTag findByNameAndHouseId(String tag, Long houseId);
}
