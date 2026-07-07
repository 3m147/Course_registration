package com.coachlink.portfolio;

import com.coachlink.portfolio.security.jwt.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class JwtTest {
    @Autowired
    private JWTUtil jwtUtil;

    @BeforeEach // 테스트 어노테이션이 붙은 메서드를 수행하기 이전에 수행하는 메서드임.
    public void testBefore() {
        System.out.println("testBefore=========");
//        jwtUtil = jwtUtil;
        System.out.println(jwtUtil.toString());
    }

    @Test
    public void testEncode() throws Exception{
        String email = "user100@example.com";
        List<String> roles = Arrays.asList("PLAYER", "MEMBER");
        String str = jwtUtil.generateToken(email, roles);

        System.out.println(str);
    }

    @Test
    public void testValidate() throws Exception{
        String email = "user100@example.com";
        List<String> roles = Arrays.asList("PLAYER", "MEMBER");
        String str = jwtUtil.generateToken(email, roles);

        Thread.sleep(3000);
        String resultEmail = jwtUtil.validateAndExtract(str);

        System.out.println("resultEmail: " +resultEmail);
    }
}
