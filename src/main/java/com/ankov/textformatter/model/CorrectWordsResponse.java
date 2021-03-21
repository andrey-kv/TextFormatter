package com.ankov.textformatter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CorrectWordsResponse {
    private int totalCount;
    List<Correction> corrections;
}
