package com.example.tickets.dto;

import com.example.tickets.enumeration.HttpResponseEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class HttpResponse {
    private int code;
    private String message;

    public HttpResponse(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public HttpResponse(HttpResponseEnum responseEnum) {
        super();
        this.code = responseEnum.getId();
        this.message = responseEnum.getDescription();
    }
}
