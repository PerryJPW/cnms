package com.perry.cnms.entity;

/**
 * @Author: PerryJ
 * @Date: 2020/2/10
 */
public class Settings {
    private Integer settingId;
    private String adminPassword;
    private Double acceptXy;
    private Double acceptH;

    public Integer getSettingId() {
        return settingId;
    }

    public void setSettingId(Integer settingId) {
        this.settingId = settingId;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Double getAcceptXy() {
        return acceptXy;
    }

    public void setAcceptXy(Double acceptXy) {
        this.acceptXy = acceptXy;
    }

    public Double getAcceptH() {
        return acceptH;
    }

    public void setAcceptH(Double acceptH) {
        this.acceptH = acceptH;
    }
}
