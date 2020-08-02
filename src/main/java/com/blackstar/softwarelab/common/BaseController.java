package com.blackstar.softwarelab.common;

import com.blackstar.softwarelab.exception.ErrorCode;
import com.blackstar.softwarelab.exception.ErrorResponse;
import com.blackstar.softwarelab.bean.SecurityUser;
import com.blackstar.softwarelab.entity.SysUser;
import com.blackstar.softwarelab.service.ISysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public abstract class BaseController {


    @Autowired
    private ISysUserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public SecurityUser getSecurityUser() {

        SysUser user = userService.getById("205635b9-ab37-43cb-82c2-811a58880fa1");

        return SecurityUser.builder()
                .id(user.getId())
                .mail(user.getMail())
                .username(user.getUsername())
                .build();
    }


    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException ex, HttpServletResponse response) {
        log.error(ex.getMessage());
        try {
            objectMapper.writeValue(response.getWriter(),
                    ErrorResponse.of(ex.getMessage(),
                            ErrorCode.GENERAL, HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (IOException e) {
            log.error("Can't handle exception",e);
        }
    }

}
