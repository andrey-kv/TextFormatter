package com.ankov.textformatter.services;

import com.ankov.textformatter.exceptions.BadRequestException;
import com.ankov.textformatter.exceptions.SettingNotFoundException;
import com.ankov.textformatter.model.Setting;
import com.ankov.textformatter.repositories.SettingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingServiceImpl implements  SettingService {

    private final SettingRepository settingRepository;

    @Override
    public String getValue(String key, String errorMessage) {
        Setting setting = settingRepository.findById(key)
                .orElseThrow(() -> new SettingNotFoundException(errorMessage));
        return setting.getValue();
    }

    @Override
    public void storeValue(String key, String value) {
        Setting setting = settingRepository.findById(key)
                .orElse(new Setting(key, value));
        setting.setValue(value);
        settingRepository.save(setting);
    }
}


