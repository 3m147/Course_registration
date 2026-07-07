package com.coachlink.portfolio.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {


    public ApiLoginFilter(String defaultFilterProcessesUrl) {
        super(request ->
                "/auth/login".equals(request.getServletPath())
                        && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException {

        log.info("---api login filter---");

        ObjectMapper om = new ObjectMapper();
        Map<String, String> json = om.readValue(request.getInputStream(), Map.class);

        String username = json.get("username");
        String password = json.get("userPwd");

        if (username == null || password == null) {
            throw new BadRequestException("username or password cannot be null");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(authToken);
    }

}
