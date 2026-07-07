package com.coachlink.portfolio.security.handler;

import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.repository.SportNameRepository;
import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.security.dto.MemberAuthDTO;
import com.coachlink.portfolio.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final String successRedirectUrl;

    public OAuthAuthenticationSuccessHandler(JWTUtil jwtUtil, UserRepository userRepository, String successRedirectUrl) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.successRedirectUrl = successRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = (String) oAuth2User.getAttributes().get("username");
        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));


        String mainName = "";
        String subName = "";
        if (member.getSportName() != null) {
            mainName = member.getSportName().getMainName();
            subName = member.getSportName().getSubName();}

//        String role = member.getRoleSet().isEmpty() ? "ROLE_MEMBER" : "ROLE_"+member.getRoleSet().iterator().next().name();
        List<String> roles = member.getRoleSet().isEmpty()
                ? List.of("ROLE_MEMBER")
                : member.getRoleSet().stream()
                .map(role -> "ROLE_" + role.name())
                .collect(Collectors.toList());

        String token = null;
        log.info("username : " + username);
        log.info("role : " + member.getRoleSet());

        StringBuilder role = new StringBuilder();

        for (String r : roles) {
            role.append("&role=");
            role.append(URLEncoder.encode(r, StandardCharsets.UTF_8));
        }

        try {
            token = jwtUtil.generateToken(username, roles);

            String redirectUrl = successRedirectUrl + "?"
                    + "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                    + "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + "&mainName=" + URLEncoder.encode(mainName, StandardCharsets.UTF_8)
                    + "&subName=" + URLEncoder.encode(subName, StandardCharsets.UTF_8)
                    + role;
            log.info(redirectUrl);
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
