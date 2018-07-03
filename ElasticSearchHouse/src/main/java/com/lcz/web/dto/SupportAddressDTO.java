package com.lcz.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SupportAddressDTO {
	 private Long id;
    @JsonProperty(value = "belong_to")
    private String belongTo;

    @JsonProperty(value = "en_name")
    private String enName;

    @JsonProperty(value = "cn_name")
    private String cnName;

    private String level;

    private double baiduMapLongitude;

    private double baiduMapLatitude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(String belongTo) {
		this.belongTo = belongTo;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public double getBaiduMapLongitude() {
		return baiduMapLongitude;
	}

	public void setBaiduMapLongitude(double baiduMapLongitude) {
		this.baiduMapLongitude = baiduMapLongitude;
	}

	public double getBaiduMapLatitude() {
		return baiduMapLatitude;
	}

	public void setBaiduMapLatitude(double baiduMapLatitude) {
		this.baiduMapLatitude = baiduMapLatitude;
	}
    
    
}
