package com.ankov.textformatter.exceptions;

public class SettingNotFoundException extends RuntimeException {
    public SettingNotFoundException(String message) {
        super(message);
    }
}
