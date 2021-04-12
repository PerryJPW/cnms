package com.perry.cnms.dto;

import com.perry.cnms.entity.Area;
import com.perry.cnms.entity.AreaUsing;
import com.perry.cnms.enums.StateEnum;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/16
 */
public class AreaUseExecution {
    private int state;
    private String stateInfo;
    private int count;
    private AreaUsing areaUsing;
    private List<AreaUsing> areaUsingList;
    private Integer areaId;


    public AreaUseExecution() {
    }

    public AreaUseExecution(StateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public AreaUseExecution(StateEnum stateEnum, AreaUsing areaUsing) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.areaUsing = areaUsing;
        this.areaId= areaUsing.getAreaId();
    }

    public AreaUseExecution(StateEnum stateEnum, int areaId) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.areaId = areaId;
    }


    public AreaUseExecution(StateEnum stateEnum, List<AreaUsing> areaUsingList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.areaUsingList = areaUsingList;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public AreaUsing getAreaUsing() {
        return areaUsing;
    }

    public void setAreaUsing(AreaUsing areaUsing) {
        this.areaUsing = areaUsing;
    }

    public List<AreaUsing> getAreaUsingList() {
        return areaUsingList;
    }

    public void setAreaUsingList(List<AreaUsing> areaUsingList) {
        this.areaUsingList = areaUsingList;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
