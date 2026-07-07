package com.coachlink.portfolio.security.service;

import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.security.dto.OAuthAttributes;
import com.coachlink.portfolio.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthUserDetailsService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 소셜로그인 서비스에 사용자 정보 요청 객체 생성
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인 업체의 소셜ID를 얻어옴 (google, naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 로그인 업체에서 제공하는 사용자 정보를 가져와서 String 으로 변환
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 소셜로그인 업체에 따라 JSON을 파싱해서 OAuthAttributes
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        saveOrUpdate(attributes);

        Member member = userRepository.findByUsername(attributes.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        String mainName = "";
        String subName = "";

        if (member.getSportName() != null) {
            mainName = member.getSportName().getMainName();
            subName = member.getSportName().getSubName();}

        Collection<GrantedAuthority> authorities = member.getRoleSet().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());


        if (authorities.isEmpty()) {
            member.addMemberRole(Role.MEMBER);
            userRepository.save(member);
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"));
        }

        // 5. DefaultOAuth2User 반환 (Member 정보 attributes에 포함)
        Map<String, Object> memberAttributes = new HashMap<>();
        memberAttributes.put("username", member.getUsername());
        memberAttributes.put("name", member.getName());
        memberAttributes.put("mainName", mainName);
        memberAttributes.put("subName", subName);
        memberAttributes.put("fromSocial", member.isFromSocial());

        return new DefaultOAuth2User(
                authorities,
                memberAttributes,
                "username");
    }

    private void saveOrUpdate(OAuthAttributes attributes) {
        Optional<Member> result = userRepository.findByEmail(attributes.getEmail(), true);

        // 기존 회원
        if (result.isPresent()) {
            Member entity = result.get();

            // 필요한 정보만 갱신
            entity.setName(attributes.getName());
            entity.setProvider(attributes.getProvider());
            entity.setFromSocial(true);

            log.info(entity.toString());
            userRepository.save(entity);
            return;
        }

        // 신규 회원
        Member newMember = Member.builder()
                .username(attributes.getEmail())
                .email(attributes.getEmail())
                .userPwd("SOCIAL_USER")
                .fromSocial(true)
                .name(attributes.getName())
                .provider(attributes.getProvider())
                .roleSet(new HashSet<>())
                .build();

        newMember.addMemberRole(Role.MEMBER);  // 기본 권한 부여
        userRepository.save(newMember);
    }


}
