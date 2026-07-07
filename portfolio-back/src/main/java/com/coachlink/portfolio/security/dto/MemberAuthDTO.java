package com.coachlink.portfolio.security.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Slf4j
@Getter
@Setter
@ToString
public class MemberAuthDTO extends User {

    private String username;
    private String name;
    private Boolean fromSocial;
    private String mainName;
    private String subName;
    private String role;

    public MemberAuthDTO(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean fromSocial, String mainName, String subName, String role) {

        super(username, password, authorities);

        this.username = username;
        this.fromSocial = fromSocial;
        this.mainName = mainName;
        this.subName = subName;
        this.role = role;
    }
}
