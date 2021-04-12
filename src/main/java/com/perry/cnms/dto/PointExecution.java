package com.perry.cnms.dto;

import com.perry.cnms.entity.Point;
import com.perry.cnms.enums.StateEnum;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/22
 */
public class PointExecution {
    private int state;
    private String stateInfo;
    private int count;
    private Point point;
    private List<Point> pointList;




    public PointExecution(StateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public PointExecution(StateEnum stateEnum,Point point) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.point=point;
    }
    public PointExecution(StateEnum stateEnum,List<Point> pointList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.pointList=pointList;
    }

    public PointExecution() {
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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }
}
