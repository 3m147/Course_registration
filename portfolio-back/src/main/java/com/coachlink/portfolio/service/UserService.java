package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.MemberDTO;
import com.coachlink.portfolio.dto.PasswordChangeDTO;
import com.coachlink.portfolio.entity.Member;

public interface UserService {

    Member registerUser(MemberDTO memberDTO);

    Member updateUser(String username, Member updatedMemberData);

    boolean deleteUser(String username);

    Member getUserDetail(String username);


    boolean changePassword(PasswordChangeDTO dto);

    boolean existsByUsername(String username);
}
