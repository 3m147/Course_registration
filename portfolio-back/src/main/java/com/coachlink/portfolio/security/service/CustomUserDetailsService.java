package com.coachlink.portfolio.security.service;

import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.entity.SportName;
import com.coachlink.portfolio.repository.SportNameRepository;
import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.security.dto.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SportNameRepository sportNameRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: " + username);

        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        String mainName = "";
        String subName = "";

        if (member.getSportName() != null) {
            mainName = member.getSportName().getMainName();
            subName = member.getSportName().getSubName();
        }

        // SportName sportName =
        // sportNameRepository.findById(member.getSportName().getSportId()).orElseThrow(()
        // ->
        // new IllegalStateException("해당 회원의 종목 정보를 찾을 수 없습니다."));

        String roleStr = member.getRoleSet().stream()
                .findFirst()
                .map(Enum::name)
                .orElse("MEMBER");

        java.util.Set<org.springframework.security.core.GrantedAuthority> authorities = member.getRoleSet().isEmpty() ?
                java.util.Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")) :
                member.getRoleSet().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toSet());

        MemberAuthDTO memberAuthDTO = new MemberAuthDTO(
                member.getUsername(), member.getUserPwd(),
                authorities,
                member.isFromSocial(),
                mainName,
                subName,
                roleStr);

        memberAuthDTO.setName(member.getName());
        return memberAuthDTO;
    }
}
