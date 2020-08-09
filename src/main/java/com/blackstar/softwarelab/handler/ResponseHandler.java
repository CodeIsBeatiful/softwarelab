package com.blackstar.softwarelab.handler;

import com.blackstar.softwarelab.bean.Code;
import com.blackstar.softwarelab.bean.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;

@RestControllerAdvice
public class ResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        //support all
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter,
                                  MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //catch spring exception
        if (o instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap<String,Object>) o;
            if(map.get("error")!=null){
                return Response.of(String.valueOf(map.get("message")), Code.GENERAL);
            }
        }
        return Response.ofSuccess(o);
    }
}
