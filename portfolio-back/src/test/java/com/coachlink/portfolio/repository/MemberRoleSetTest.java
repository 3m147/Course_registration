package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class MemberRoleSetTest {
	@Autowired
	private UserRepository userRepository;

	// @Test
	// @Transactional
	// @Commit
	// void migrateRoleToRoleSet() {
	// // 1) 모든 회원 조회
	// List<Member> members = userRepository.findAll();

	// // 2) role -> roleSet
	// for (Member member : members) {
	// Role singleRole = member.getRole();
	// if (singleRole != null) {
	// // roleSet에 없는 경우 직접 add() 호출
	// member.getRoleSet().add(singleRole);
	// }
	// }
	// // 3) DB 저장
	// userRepository.saveAll(members);
	// }
}
