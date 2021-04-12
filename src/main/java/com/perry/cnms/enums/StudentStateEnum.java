package com.perry.cnms.enums;

/**
 * @Author: PerryJ
 * @Date: 2020/2/5
 */
public enum StudentStateEnum {
    NO_STATE(0, "不可用"), INITIAL(1, "初始状态"), PERIOD_ZERO(2, "已分配区域"), PERIOD_ONE_A(3, "P1 提交未检查"), PERIOD_ONE_B(4, "P1 不合格)"), PERIOD_ONE_C(5, "P1 合格"), PERIOD_TWO_A(6, "P2 提交未检查"),PERIOD_TWO_B(7, "P2 不合格"), PERIOD_TWO_C(8, "P2 合格"), HISTORY(99, "历史账号");
    private int state;
    private String stateInfo;

    private StudentStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static StudentStateEnum stateOf(int state) {
        for (StudentStateEnum studentStateEnum : values()) {
            if (studentStateEnum.getState() == state) {
                return studentStateEnum;
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
