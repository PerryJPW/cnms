package com.perry.cnms.enums;

/**
 * @Author: PerryJ
 * @Date: 2020/1/16
 */
public enum StateEnum {
    SUCCESS(1, "操作成功"), EMPTY_PARAM(-1, "参数为空"), EMPTY_RETURN(-2, "返回数据为空"), INNER_ERROR(-1001, "内部错误"), NULL_AREA_ID(-1002, "AREA_ID为空"), NULL_POINT_ID(-1003, "POINT_ID为空"), NULL_TEACHER_ID(-1004, "TEACHER_ID为空"), NULL_GROUP_ID(-1005, "GROUP_ID为空");
    private int state;
    private String stateInfo;

    private StateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static StateEnum stateOf(int state) {
        for (StateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }
}
