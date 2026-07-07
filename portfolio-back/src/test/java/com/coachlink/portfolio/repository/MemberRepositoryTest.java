package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.SportName;
import com.coachlink.portfolio.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    UserRepository userRepository;

//    @Test
//    public void insertUser() {
//        userRepository.save(Member.builder()
//                .username("dooly")
//                .name("둘리")
//                .userPwd("1234")
//                .sportName(SportName.builder().sportId(1L).build())
//                .build());
//    }


//    @Test
//    public void selectUserByUserName() {
//        Member member = userRepository.findByUsername("dooly");
//        System.out.println(member.toString());
//    }

}
