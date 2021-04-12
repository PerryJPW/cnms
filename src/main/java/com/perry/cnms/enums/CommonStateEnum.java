package com.perry.cnms.enums;

/**
 * @Author: PerryJ
 * @Date: 2020/2/5
 */
public enum CommonStateEnum {
    NO_STATE(0, "不可用"), INITIAL(1, "正常（初始）状态"),   HISTORY(99, "历史账号");
    private int state;
    private String stateInfo;

    private CommonStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static CommonStateEnum stateOf(int state) {
        for (CommonStateEnum commonStateEnum : values()) {
            if (commonStateEnum.getState() == state) {
                return commonStateEnum;
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
