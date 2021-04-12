package com.perry.cnms.dao;

import com.perry.cnms.entity.Settings;

/**
 * @Author: PerryJ
 * @Date: 2020/2/10
 */
public interface SettingsDao {
    Settings querySettings();

    int updateSettings(Settings settings);
}
