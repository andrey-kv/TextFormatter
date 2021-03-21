package com.ankov.textformatter.services;

public interface SettingService {
    String getValue(String key, String errorMessage);
    void storeValue(String key, String value);
}
