package com.blackstar.softwarelab.common;

import com.blackstar.softwarelab.bean.Code;
import com.blackstar.softwarelab.bean.Response;
import com.blackstar.softwarelab.bean.SecurityUser;
import com.blackstar.softwarelab.bean.SortObj;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public abstract class BaseController {


    private ObjectMapper objectMapper = new ObjectMapper();

    public SecurityUser getSecurityUser() {

        return getCurrentUser();
    }


    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException ex, HttpServletResponse response) {
        log.error("catch runtime exception:", ex);
        try {
            objectMapper.writeValue(response.getWriter(),
                    Response.of(ex.getMessage(),
                            Code.GENERAL, null));
        } catch (IOException e) {
            log.error("Can't handle exception", e);
        }
    }

    protected SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            return null;
//            throw new Exception("您无权进行此操作!", Code.AUTHENTICATION);
        }
    }

    protected SortObj checkSort(String[] canSortStr, String sort) {

        String order;
        if (sort == null) {
            return null;
        }
        if (sort.length() < 2) {
            throw new RuntimeException("sort length less to 2");
        }
        // - asc + desc
        String orderStr = sort.substring(0, 1);
        if (orderStr.equals("+")) {
            order = "asc";
        } else if (orderStr.equals("-")) {
            order = "desc";
        } else {
            throw new RuntimeException("unsupported order type: " + orderStr);
        }
        String keywordStr = sort.substring(1);
        boolean canSortFlag = false;
        for (String str : canSortStr) {
            if (str.equals(keywordStr)) {
                canSortFlag = true;
            }
        }
        if (!canSortFlag) {
            throw new RuntimeException("unsupported sort keyword: " + keywordStr);
        }
        return SortObj.builder()
                .key(keywordStr)
                .value(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,keywordStr))
                .order(order)
                .build();
    }

}
