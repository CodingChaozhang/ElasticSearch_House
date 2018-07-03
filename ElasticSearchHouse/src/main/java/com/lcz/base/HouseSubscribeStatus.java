package com.lcz.base;

import com.lcz.entity.HouseSubscribe;

/**
 * 预约状态码
 * Created by codingchaozhang.
 */
public enum HouseSubscribeStatus {
    NO_SUBSCRIBE(0), // 未预约
    IN_ORDER_LIST(1), // 已加入待看清单
    IN_ORDER_TIME(2), // 已经预约看房时间
    FINISH(3); // 已完成预约

    private int value;

    HouseSubscribeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HouseSubscribeStatus of(int value) {
        for (HouseSubscribeStatus status : HouseSubscribeStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return HouseSubscribeStatus.NO_SUBSCRIBE;
    }
}
