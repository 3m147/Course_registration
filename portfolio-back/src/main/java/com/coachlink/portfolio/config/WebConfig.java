package com.coachlink.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Value("${app.upload.image-dir}")
	private String uploadImageDir;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 어떤 URL 패턴을 적용할 것인지
				.allowedOrigins(allowedOrigins) // 허용할 front
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 메서드 상태
				.allowCredentials(true); // 쿠키도 허용
	}
	
	@Override
	public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")
				.addResourceLocations("file:" + uploadImageDir + "/");
	}
}
