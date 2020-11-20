package com.softwarelab.application.config.jwt;

import java.io.Serializable;

public interface JwtToken extends Serializable {
    String getToken();
}
