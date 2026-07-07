package com.coachlink.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

    private String username;
    private String name;
    private String userPwd;
    private String email;
    private String address;
    private String gender;
    private String role;
    private Long sportId;
    
}
