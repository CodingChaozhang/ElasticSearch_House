package com.lcz.web.form;

/**
 * Created by codingchaozhang.
 */
public class MapSearch {
    private String cityEnName;

    /**
     * 地图缩放级别
     */
    private int level = 12;
    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";
    /**
     * 左上角
     */
    private Double leftLongitude;
    private Double leftLatitude;

    /**
     * 右下角
     */
    private Double rightLongitude;
    private Double rightLatitude;

    private int start = 0;
    private int size = 5;

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    public Double getLeftLongitude() {
        return leftLongitude;
    }

    public void setLeftLongitude(Double leftLongitude) {
        this.leftLongitude = leftLongitude;
    }

    public Double getLeftLatitude() {
        return leftLatitude;
    }

    public void setLeftLatitude(Double leftLatitude) {
        this.leftLatitude = leftLatitude;
    }

    public Double getRightLongitude() {
        return rightLongitude;
    }

    public void setRightLongitude(Double rightLongitude) {
        this.rightLongitude = rightLongitude;
    }

    public Double getRightLatitude() {
        return rightLatitude;
    }

    public void setRightLatitude(Double rightLatitude) {
        this.rightLatitude = rightLatitude;
    }

    public int getStart() {
        return start < 0 ? 0 : start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size > 100 ? 100 : size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
