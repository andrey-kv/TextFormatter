package com.ankov.textformatter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponse {
    private String reason;
    private String message;
}
