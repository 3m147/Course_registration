package com.coachlink.portfolio.security.handler;

import com.coachlink.portfolio.security.dto.MemberAuthDTO;
import com.coachlink.portfolio.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public LoginAuthenticationSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        MemberAuthDTO memberAuthDTO = (MemberAuthDTO) authentication.getPrincipal();

        String username = memberAuthDTO.getUsername();
//        String role = memberAuthDTO.getAuthorities().iterator().next().getAuthority();
        List<String> roles = memberAuthDTO.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String mainName = memberAuthDTO.getMainName();
        String subName = memberAuthDTO.getSubName();
        String token = null;
        log.info("username : " + username);
        log.info("role : " + roles);
        try {
            token = jwtUtil.generateToken(username, roles);
            response.setContentType("application/json; charset=UTF-8");

            JSONObject json = new JSONObject();
            json.put("token", token);
            json.put("username", username);
            json.put("roles", roles);
            json.put("mainName", mainName);
            json.put("subName", subName);

            response.getWriter().print(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}


