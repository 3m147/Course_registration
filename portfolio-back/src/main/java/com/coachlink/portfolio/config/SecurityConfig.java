package com.coachlink.portfolio.config;

import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.security.filter.ApiCheckFilter;
import com.coachlink.portfolio.security.filter.ApiLoginFilter;
import com.coachlink.portfolio.security.handler.LoginAuthenticationFailureHandler;
import com.coachlink.portfolio.security.handler.LoginAuthenticationSuccessHandler;
import com.coachlink.portfolio.security.handler.OAuthAuthenticationSuccessHandler;
import com.coachlink.portfolio.security.jwt.JWTUtil;
import com.coachlink.portfolio.security.service.CustomUserDetailsService;
import com.coachlink.portfolio.security.service.OAuthUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import java.util.Arrays;


@Configuration
@EnableWebSecurity
@Slf4j
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private JWTUtil jwtUtil;
    private OAuthUserDetailsService oAuthUserDetailsService;
    private CustomUserDetailsService customUserDetailsService;
    private UserRepository userRepository;
    private String[] allowedOrigins;
    private String oauthSuccessRedirectUrl;


    public SecurityConfig(JWTUtil jwtUtil,
                          OAuthUserDetailsService oAuthUserDetailsService,
                          CustomUserDetailsService customUserDetailsService,
                          UserRepository userRepository,
                          @Value("${app.cors.allowed-origins}") String[] allowedOrigins,
                          @Value("${app.oauth.success-redirect-url}") String oauthSuccessRedirectUrl) {
        this.jwtUtil = jwtUtil;
        this.oAuthUserDetailsService = oAuthUserDetailsService;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.allowedOrigins = allowedOrigins;
        this.oauthSuccessRedirectUrl = oauthSuccessRedirectUrl;
    }

    private final String[] USER_PUBLIC_URL = {
            "/member/checkId",
            "/member/register",
            "/reviews/lecture/**",
            "/reviews/detail/**"
    };

    private final String[] ALL_URL = {
            // GET
            "/",
            "/lecture/**",
            "/player/**",

            "/member/profile/**",
            "/reviews/**",
            "/review-images/**",
            "/profiles/**",
            "/profile-img/**", // Added explicit permission here
            "/images/**",
            "/api/sports/**",


            // POST
            "/auth/signup",
            "/auth/login"
    };

    private final String[] PLAYER_URL = {
            "/auth/refresh",
            "/auth/logout"
    };

    private final String[] MEMBER_URL = {
            "/enroll/**",
            "/lecture/**",
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        // PasswordEncoder의 객체로 BCrypt 암호화 방식의 인코더 객체를 사용
         return new BCryptPasswordEncoder();
        // return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public ApiCheckFilter apiCheckFilter() {
        String[] checkPatterns = {
                "/auth/refresh",
                "/auth/logout",
                "/member/me/**/*",
                "/enroll/**",
                "/member/password-change",
                "/member/**",
                "/reviews/**"
        };

        return new ApiCheckFilter(checkPatterns, USER_PUBLIC_URL, jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // URL 접근 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(ALL_URL).permitAll()
                .requestMatchers(USER_PUBLIC_URL).permitAll()
                .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/reviews/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/reviews").authenticated()
                .requestMatchers("/member/**").authenticated()
                .requestMatchers(MEMBER_URL).hasRole("MEMBER")
                .requestMatchers(PLAYER_URL).hasRole("PLAYER")
                .requestMatchers("/member/me/**").hasAnyRole("MEMBER", "PLAYER")
                .anyRequest().hasRole("ADMIN") // 외 모든 요청은 관리자 권한
        );

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        // CSRF 비활성화: JWT 기반 API 서버에서는 보통 사용하지 않음
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        // API 인증/권한 체크 필터 추가
        http.addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        // Spring Security가 WebConfig의 CORS 설정을 사용하도록 함.
        http.cors(cors -> {
        });

        // AuthenticationManager 설정: 로그인 요청 시 인증 처리를 담당
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        // 커스텀 로그인 필터 생성
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/auth/login");
        apiLoginFilter.setAuthenticationManager(authenticationManager);
        apiLoginFilter.setAuthenticationFailureHandler(new LoginAuthenticationFailureHandler());
        apiLoginFilter.setAuthenticationSuccessHandler(new LoginAuthenticationSuccessHandler(jwtUtil));
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        // OAuth2
        http.oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")  // 명시적 로그인 페이지 설정
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuthUserDetailsService)
                        )
                        .successHandler(new OAuthAuthenticationSuccessHandler(jwtUtil, userRepository, oauthSuccessRedirectUrl))
                        .failureHandler(new LoginAuthenticationFailureHandler())
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
