package com.insider.apitest.dto;

import lombok.Getter;

@Getter
public class Response {
    private int code;
    private String type;
    private String message;
}
