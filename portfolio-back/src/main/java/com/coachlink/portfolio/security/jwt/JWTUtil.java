package com.coachlink.portfolio.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtil {

    private final JWTConfig jwtConfig;

    public String generateToken(String username, List<String> roles) {

        String secretKey = jwtConfig.getSecret();
        long expireMinutes = jwtConfig.getTokenValidTime(); // 10분을 더한다?
        log.info("roles: " + roles.toString());
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now()
                        .plusMinutes(expireMinutes)
                        .toInstant()))
                .setSubject(username)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS256,
                        secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String validateAndExtract(String tokenStr) throws Exception {
        String secretKey = jwtConfig.getSecret();

        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build();

        Claims claims = parser.parseClaimsJws(tokenStr).getBody();

        log.info("============== JWT ==============");
        log.info("만료일: {}", claims.getExpiration());
        log.info("내용: {}", claims.getSubject());

        return claims.getSubject();
    }

    public List<String> extractRole(String tokenStr) {
        log.info("tokenStr", tokenStr.toString());
        String secretKey = jwtConfig.getSecret();

        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build();

        Claims claims = parser.parseClaimsJws(tokenStr).getBody();
        return claims.get("roles", List.class);
    }

}