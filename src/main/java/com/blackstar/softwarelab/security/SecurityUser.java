package com.blackstar.softwarelab.security;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SecurityUser {

    private String id;

    private String username;

    private String mail;

}
