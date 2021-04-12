package com.perry.cnms.dto;

import com.perry.cnms.entity.Area;
import com.perry.cnms.enums.StateEnum;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/16
 */
public class AreaExecution {
    private int state;
    private String stateInfo;
    private int count;
    private Area area;
    private List<Area> areaList;
    private Integer areaId;


    public AreaExecution() {
    }

    public AreaExecution(StateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public AreaExecution(StateEnum stateEnum, Area area) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.area = area;
        this.areaId=area.getAreaId();
    }

    public AreaExecution(StateEnum stateEnum, int areaId) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.areaId = areaId;
    }


    public AreaExecution(StateEnum stateEnum, List<Area> areaList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.areaList = areaList;
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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public List<Area> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Area> areaList) {
        this.areaList = areaList;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }
}
