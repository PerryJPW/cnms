package com.perry.cnms.service;

import com.perry.cnms.entity.Settings;

/**
 * @Author: PerryJ
 * @Date: 2020/2/10
 */
public interface SettingsService {
    String getAdminPassword();

    Double[] getAccept();

    int setSettings(Settings settings);
}
