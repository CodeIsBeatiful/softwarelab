package com.blackstar.softwarelab.bean;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Code {

    SUCCESS(-1),
    GENERAL(2),
    AUTHENTICATION(10),
    JWT_TOKEN_EXPIRED(11),
    PERMISSION_DENIED(20),
    INVALID_ARGUMENTS(30),
    BAD_REQUEST_PARAMS(31);

    private int code;

    Code(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

}
