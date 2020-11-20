package com.softwarelab.application.bean;

import java.util.Date;

public class Response {

    // General  message
    private final String message;

    //  code
    private final Code code;

    private final Object data;

    private final Date timestamp;

    protected Response(final String message, final Code code) {
        this.message = message;
        this.code = code;
        this.timestamp = new java.util.Date();
        this.data = null;
    }

    protected Response(final String message, final Code code, Object data) {
        this.message = message;
        this.code = code;
        this.timestamp = new java.util.Date();
        this.data = data;
    }

    public static Response of(final String message, final Code code) {
        return new Response(message, code);
    }

    public static Response of(final String message, final Code code, Object data) {
        return new Response(message, code, data);
    }

    public static Response ofSuccess(Object data){
        return new Response(null, Code.SUCCESS,  data);
    }

    public String getMessage() {
        return message;
    }

    public Code getCode() {
        return code;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Object getData() {
        return data;
    }
}
