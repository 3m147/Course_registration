package com.coachlink.portfolio.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        json.put("code", "401");
        json.put("message", exception.getMessage());
        PrintWriter out = response.getWriter();
        out.print(json);
        return;
    }
}
