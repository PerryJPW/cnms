package com.perry.cnms.service.impl;

import com.perry.cnms.dao.SettingsDao;
import com.perry.cnms.entity.Settings;
import com.perry.cnms.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: PerryJ
 * @Date: 2020/2/10
 */
@Service
public class SettingsServiceImpl implements SettingsService {
    @Autowired
    SettingsDao settingsDao;

    @Override
    public String getAdminPassword() {
        try {
            Settings settings = settingsDao.querySettings();
            if (settings != null) {
                return settings.getAdminPassword();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double[] getAccept() {
        try {
            Settings settings = settingsDao.querySettings();
            if (settings != null) {
                Double[] accept=new Double[2];
                accept[0]=settings.getAcceptXy();
                accept[1]=settings.getAcceptH();
                return accept;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int setSettings(Settings settings) {
        try {
            int effectNum= settingsDao.updateSettings(settings);
            if (effectNum ==1) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;
    }
}
