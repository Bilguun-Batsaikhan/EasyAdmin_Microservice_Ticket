package com.certimeter.tickets.exception;

import com.certimeter.tickets.enumeration.HttpResponseEnum;

public class FailureException extends RuntimeException {
    private final HttpResponseEnum httpResponseEnum;
    private final String customMessage;

    public FailureException(HttpResponseEnum httpResponseEnum) {
        super(httpResponseEnum.getDescription());
        this.httpResponseEnum = httpResponseEnum;
        this.customMessage = null; // No custom message provided
    }

    public FailureException(HttpResponseEnum httpResponseEnum, String customMessage) {
        super(customMessage != null ? customMessage : httpResponseEnum.getDescription());
        this.httpResponseEnum = httpResponseEnum;
        this.customMessage = customMessage;
    }

    public HttpResponseEnum getHttpResponseEnum() {
        return httpResponseEnum;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}


