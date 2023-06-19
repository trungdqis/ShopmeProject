package com.shopme.common.entity;

import java.util.List;

public class SettingBag {

    private List<Setting> settings;

    public SettingBag() {}

    public SettingBag(List<Setting> settings) {
        this.settings = settings;
    }

    public Setting get(String key) {
        int index = settings.indexOf(new Setting(key));

        if (0 <= index) {
            return settings.get(index);
        }

        return null;
    }

    public String getValue(String key) {
        Setting setting = get(key);

        if (null != setting) {
            return setting.getValue();
        }

        return null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);

        if (null != setting && null != value) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return settings;
    }
}
