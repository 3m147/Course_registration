package com.coachlink.portfolio.security.filter;

import com.coachlink.portfolio.security.jwt.JWTUtil;
import com.coachlink.portfolio.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
public class ApiCheckFilter extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher;
    private final String[] patterns;
    private final String[] excludePatterns;
    private JWTUtil jwtUtil;

    public ApiCheckFilter(String[] patterns, String[] excludePatterns, JWTUtil jwtUtil) {
        this.antPathMatcher = new AntPathMatcher();
        this.patterns = patterns;
        this.excludePatterns = excludePatterns;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        for (String pattern : patterns) {
            if (antPathMatcher.match(pattern, request.getRequestURI())) {
                boolean isExcluded = false;
                if (excludePatterns != null) {
                    for (String exclude : excludePatterns) {
                        if (antPathMatcher.match(exclude, request.getRequestURI())) {
                            isExcluded = true;
                            break;
                        }
                    }
                }
                if (isExcluded) {
                    continue;
                }

                boolean checkHeader = false;
                try {
                    checkHeader = checkAuthHeader(request);
                } catch (Exception e) {
                    log.error("Token validation failed", e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=utf-8");
                    JSONObject json = new JSONObject();
                    json.put("code", "401");
                    json.put("message", "INVALID OR EXPIRED TOKEN: " + e.getMessage());
                    PrintWriter out = response.getWriter();
                    out.print(json);
                    return;
                }
                if (checkHeader) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=utf-8");
                    JSONObject json = new JSONObject();
                    json.put("code", "401");
                    json.put("message", "FAIL CHECK API TOKEN");
                    PrintWriter out = response.getWriter();
                    out.print(json);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkAuthHeader(HttpServletRequest request) throws Exception {
        boolean result = false;
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.validateAndExtract(token);
            List<String> roles = jwtUtil.extractRole(token);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // java.util.List<SimpleGrantedAuthority> authorities =
            // java.util.Arrays.stream(role.split(","))
            // .map(SimpleGrantedAuthority::new)
            // .collect(java.util.stream.Collectors.toList());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return true; // 토큰만 유효하면 다음 필터로 진행
        }
        return false;
    }
}
