package com.coachlink.portfolio.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JWTConfig {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration_time}")
    private Long tokenValidTime;
    @Value("${jwt.refresh.expiration_time}")
    private Long refreshTokenValidTime;
}