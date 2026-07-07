package com.coachlink.portfolio.security.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private String provider;
    private String providerId;
    private String name;
    private String email;
    private String picture;

    private Map<String, Object> attributes;
    private String nameAttributeKey;

    @Builder
    public OAuthAttributes(String provider, String providerId, String name, String email, String picture, Map<String, Object> attributes, String nameAttributeKey) {
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    public static OAuthAttributes of (String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("google".contains(registrationId)) {
            return  ofGoogle(registrationId, userNameAttributeName, attributes);
        }

        return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider(registrationId)
                .providerId((String)attributes.get("sub"))
                .name((String)attributes.get("name"))
                .email((String)attributes.get("email"))
                .picture((String)attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
