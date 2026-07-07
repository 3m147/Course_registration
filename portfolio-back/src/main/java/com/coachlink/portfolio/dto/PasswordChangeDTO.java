package com.coachlink.portfolio.dto;

import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String username;
    private String oldPwd;
    private String newPwd;
    private String confirmPwd;
}
