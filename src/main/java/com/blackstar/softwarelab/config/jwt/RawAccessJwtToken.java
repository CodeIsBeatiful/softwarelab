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
package com.blackstar.softwarelab.config.jwt;


import com.blackstar.softwarelab.exception.JwtExpiredTokenException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.Serializable;

@Slf4j
public class RawAccessJwtToken implements JwtToken, Serializable {

    private static final long serialVersionUID = -797397445703066079L;

    private String token;

    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    /**
     * Parses and validates JWT Token signature.
     *
     * @throws BadCredentialsException
     * @throws JwtExpiredTokenException
     *
     */
    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            log.error("Invalid JWT Token", ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            log.info("JWT Token is expired", expiredEx);
            throw new JwtExpiredTokenException(this, "JWT Token expired", expiredEx);
        }
    }

    @Override
    public String getToken() {
        return token;
    }
}
