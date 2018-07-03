package com.lcz.web.form;

/**
 * 租房请求参数结构体
 * Created by codingchaozhang.
 */
public class RentSearch {
    private String cityEnName;
    private String regionEnName;
    private String priceBlock;
    private String areaBlock;
    private int room;
    private int direction;
    private String keywords;
    private int rentWay = -1;
    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private int start = 0;

    private int size = 5;

    public String getCityEnName() {
        return cityEnName;
    }

    public void setCityEnName(String cityEnName) {
        this.cityEnName = cityEnName;
    }

    public int getStart() {
        return start > 0 ? start : 0;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        if (this.size < 1) {
            return 5;
        } else if (this.size > 100) {
            return 100;
        } else {
            return this.size;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getRegionEnName() {
        return regionEnName;
    }

    public void setRegionEnName(String regionEnName) {
        this.regionEnName = regionEnName;
    }

    public String getPriceBlock() {
        return priceBlock;
    }

    public void setPriceBlock(String priceBlock) {
        this.priceBlock = priceBlock;
    }

    public String getAreaBlock() {
        return areaBlock;
    }

    public void setAreaBlock(String areaBlock) {
        this.areaBlock = areaBlock;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getRentWay() {
        if (rentWay > -2 && rentWay < 2) {
            return rentWay;
        } else {
            return -1;
        }
    }

    public void setRentWay(int rentWay) {
        this.rentWay = rentWay;
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

    @Override
    public String toString() {
        return "RentSearch {" +
                "cityEnName='" + cityEnName + '\'' +
                ", regionEnName='" + regionEnName + '\'' +
                ", priceBlock='" + priceBlock + '\'' +
                ", areaBlock='" + areaBlock + '\'' +
                ", room=" + room +
                ", direction=" + direction +
                ", keywords='" + keywords + '\'' +
                ", rentWay=" + rentWay +
                ", orderBy='" + orderBy + '\'' +
                ", orderDirection='" + orderDirection + '\'' +
                ", start=" + start +
                ", size=" + size +
                '}';
    }
}
