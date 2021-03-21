package com.ankov.textformatter.model;

public enum TocType {
    SECTION(0), HEADER(1), QUIZ(2), UNDEFINED(3);
    private int value;

    private TocType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
