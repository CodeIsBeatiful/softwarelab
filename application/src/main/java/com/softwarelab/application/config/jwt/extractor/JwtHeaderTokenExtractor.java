package com.softwarelab.application.config.jwt.extractor;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component(value = "jwtHeaderTokenExtractor")
public class JwtHeaderTokenExtractor implements TokenExtractor {

    public static final String JWT_TOKEN_HEADER_PARAM = "X-Authorization";

    public static final String HEADER_PREFIX = "Bearer ";

    @Override
    public String extract(HttpServletRequest request) {
        String header = request.getHeader(JWT_TOKEN_HEADER_PARAM);
        if (StringUtils.isBlank(header)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }

        if (header.length() < HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid authorization header size.");
        }

        return header.substring(HEADER_PREFIX.length(), header.length());
    }
}
