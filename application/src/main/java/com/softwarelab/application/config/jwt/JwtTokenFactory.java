package com.softwarelab.application.config.jwt;


import com.softwarelab.application.bean.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenFactory {

    private static final String USERNAME = "name";
    private static final String MAIL = "mail";
    private static final String ID = "id";


    private final JwtSettings settings;

    @Autowired
    public JwtTokenFactory(JwtSettings settings) {
        this.settings = settings;
    }

    /**
     * Factory method for issuing new JWT Tokens.
     */
    public AccessJwtToken createAccessJwtToken(SecurityUser securityUser) {
        Claims claims = Jwts.claims().setSubject(securityUser.getId());
        claims.put(USERNAME, securityUser.getUsername());
        claims.put(MAIL, securityUser.getMail());

        ZonedDateTime currentTime = ZonedDateTime.now();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(currentTime.plusSeconds(settings.getTokenExpirationTime()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token, claims);
    }

    public SecurityUser parseAccessJwtToken(RawAccessJwtToken rawAccessToken) {
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(settings.getTokenSigningKey());
        Claims claims = jwsClaims.getBody();
        SecurityUser securityUser = SecurityUser.builder()
                .id(claims.getSubject())
                .username(claims.get(USERNAME, String.class))
                .mail(claims.get(MAIL, String.class))
                .build();

        return securityUser;
    }

    public JwtToken createRefreshToken(SecurityUser securityUser) {

        ZonedDateTime currentTime = ZonedDateTime.now();

        Claims claims = Jwts.claims().setSubject(securityUser.getId());
        claims.put(USERNAME, securityUser.getUsername());
        claims.put(MAIL, securityUser.getMail());

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(currentTime.plusSeconds(settings.getRefreshTokenExpTime()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token, claims);
    }

    public SecurityUser parseRefreshToken(RawAccessJwtToken rawAccessToken) {
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(settings.getTokenSigningKey());
        Claims claims = jwsClaims.getBody();
        SecurityUser securityUser = SecurityUser.builder()
                .id(claims.getSubject())
                .username(claims.get(USERNAME, String.class))
                .mail(claims.get(MAIL, String.class))
                .build();
        return securityUser;
    }

}
