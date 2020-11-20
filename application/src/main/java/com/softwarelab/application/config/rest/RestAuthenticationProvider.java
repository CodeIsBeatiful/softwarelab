package com.softwarelab.application.config.rest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.softwarelab.application.bean.SecurityUser;
import com.softwarelab.application.entity.SysUser;
import com.softwarelab.application.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RestAuthenticationProvider implements AuthenticationProvider {


    private final BCryptPasswordEncoder encoder;
    private final ISysUserService userService;

    @Autowired
    public RestAuthenticationProvider(final ISysUserService userService, final BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        Object principal = authentication.getPrincipal();
        String username = principal.toString();
        String password = (String) authentication.getCredentials();
        return authenticateByUsernameAndPassword(username, password);
    }

    private Authentication authenticateByUsernameAndPassword(String username, String password) {
        SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (sysUser == null) {
            throw new UsernameNotFoundException("user not found: " + username);
        }
        if (!encoder.matches(password, sysUser.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        SecurityUser securityUser = SecurityUser.builder().id(sysUser.getId()).mail(sysUser.getMail()).username(username).build();

        return new UsernamePasswordAuthenticationToken(securityUser, null, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
