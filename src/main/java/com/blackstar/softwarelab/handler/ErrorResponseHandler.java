/**
 * Copyright © 2016-2019 The CPS Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blackstar.softwarelab.handler;

import com.blackstar.softwarelab.bean.Code;
import com.blackstar.softwarelab.bean.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class ErrorResponseHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper mapper;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        if (!response.isCommitted()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            mapper.writeValue(response.getWriter(),
                    Response.of("You don't have permission to perform this operation!",
                            Code.PERMISSION_DENIED, null));
        }
    }

    public void handle(Exception exception, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        log.debug("Processing exception {}", exception.getMessage(), exception);
        if (!response.isCommitted()) {
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                mapper.writeValue(response.getWriter(), Response.of(exception.getMessage(),
                        Code.GENERAL, null));
            } catch (IOException e) {
                log.error("Can't handle exception", e);
            }
        }
    }
}
