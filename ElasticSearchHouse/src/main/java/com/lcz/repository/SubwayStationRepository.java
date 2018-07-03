package com.lcz.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.lcz.entity.SubwayStation;



/**
 * Created by codingchaozhang.
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {
    List<SubwayStation> findAllBySubwayId(Long subwayId);
}
