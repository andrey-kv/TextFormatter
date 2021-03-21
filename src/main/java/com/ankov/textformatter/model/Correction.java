package com.ankov.textformatter.model;

import lombok.Data;

@Data
public class Correction {
    private int itemId;
    private int line;
    private String english;
    private String before;
    private String after;
}
