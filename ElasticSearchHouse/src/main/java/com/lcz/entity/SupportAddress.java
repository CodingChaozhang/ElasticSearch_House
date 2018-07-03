package com.lcz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "support_address")
public class SupportAddress {
	 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 上一级行政单位
    @Column(name = "belong_to")
    private String belongTo;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "cn_name")
    private String cnName;

    private String level;

    @Column(name = "baidu_map_lng")
    private double baiduMapLongitude;

    @Column(name = "baidu_map_lat")
    private double baiduMapLatitude;
    
    
    /**
     * 行政级别定义
     */
    public enum Level {
        CITY("city"),
        REGION("region");

        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }

            throw new IllegalArgumentException();
        }
    }


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
