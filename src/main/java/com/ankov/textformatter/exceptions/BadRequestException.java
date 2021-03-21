package com.ankov.textformatter.exceptions;

public class BadRequestException extends RuntimeException  {

    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
