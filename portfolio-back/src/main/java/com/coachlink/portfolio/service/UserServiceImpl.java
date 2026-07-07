package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.MemberDTO;
import com.coachlink.portfolio.dto.PasswordChangeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.entity.LectureReview;
import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.repository.LectureReviewRepository;
import com.coachlink.portfolio.repository.LectureReviewImgRepository;
import com.coachlink.portfolio.repository.LectureRepository;
import com.coachlink.portfolio.repository.LectureImgRepository;
import com.coachlink.portfolio.entity.Lecture;
import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private com.coachlink.portfolio.repository.SportNameRepository sportNameRepository;

	@Override
	public Member registerUser(MemberDTO memberDTO) {
		if (userRepository.findById(memberDTO.getUsername()).isPresent()) {
			System.out.println("이미 존재하는 회원입니다" + memberDTO.getUsername());
			return null;
		}

		com.coachlink.portfolio.entity.SportName sportName = null;
		if (memberDTO.getSportId() != null) {
			sportName = sportNameRepository.findById(memberDTO.getSportId()).orElse(null);
		}

		com.coachlink.portfolio.util.Gender genderEnum = com.coachlink.portfolio.util.Gender
				.valueOf(memberDTO.getGender());

		java.util.Set<com.coachlink.portfolio.util.Role> roleSet = new java.util.HashSet<>();
		roleSet.add(com.coachlink.portfolio.util.Role.MEMBER);

		if ("PLAYER".equals(memberDTO.getRole())) {
			roleSet.add(com.coachlink.portfolio.util.Role.PLAYER);
		}

		Member newMember = Member.builder()
				.username(memberDTO.getUsername())
				.name(memberDTO.getName())
				.userPwd(passwordEncoder.encode(memberDTO.getUserPwd()))
				.email(memberDTO.getEmail())
				.address(memberDTO.getAddress())
				.gender(genderEnum)
				.roleSet(roleSet)
				.sportName(sportName)
				.build();

		userRepository.save(newMember);
		System.out.println("회원가입 완료 : " + newMember.getUsername());

		return newMember;
	}

	@Override
	public Member updateUser(String username, Member updatedMemberData) {

		Member member = userRepository.findById(username).orElse(null);
		if (member == null) {
			System.out.println("회원을 찾을 수 없습니다: " + username);
			return null;
		}

		if (updatedMemberData.getName() != null) {
			member.setName(updatedMemberData.getName());
		}
		if (updatedMemberData.getEmail() != null) {
			member.setEmail(updatedMemberData.getEmail());
		}
		if (updatedMemberData.getAddress() != null) {
			member.setAddress(updatedMemberData.getAddress());
		}
		if (updatedMemberData.getGender() != null) {
			member.setGender(updatedMemberData.getGender());
		}
		// if (updatedMemberData.getRole() != null) {
		// member.setRole(updatedMemberData.getRole());
		// }

		userRepository.save(member);
		System.out.println("회원 정보 수정 완료: " + username);
		return member;
	}

	@Autowired
	private com.coachlink.portfolio.repository.LectureEnrollmentRepository enrollmentRepository;

	@Autowired
	private LectureReviewRepository lectureReviewRepository;

	@Autowired
	private LectureReviewImgRepository lectureReviewImgRepository;

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private LectureImgRepository lectureImgRepository;

	@Override
	public boolean deleteUser(String username) {

		if (userRepository.findById(username).isPresent()) {
			// 수강 신청 내역 먼저 삭제
			Member member = userRepository.findById(username).get();
			enrollmentRepository.deleteByMember(member);

			// 리뷰 및 리뷰 이미지 삭제
			List<LectureReview> reviews = lectureReviewRepository.findByUsername(username);
			for (LectureReview review : reviews) {
				lectureReviewImgRepository.deleteByLectureReview(review);
				lectureReviewRepository.delete(review);
			}

			userRepository.deleteById(username);
			System.out.println("탈퇴 성공하였습니다" + username);
			return true;
		} else {
			System.out.println("존재하지 않은 회원입니다" + username);
			return false;
		}
	}

	@Override
	public Member getUserDetail(String username) {
		if (userRepository.findById(username).isPresent()) {
			Member member = userRepository.findById(username).get();
			System.out.println("회원 정보를 불러왔습니다" + username);
			return member;
		} else {
			System.out.println("회원을 찾을 수 없습니다" + username);
			return null;
		}
	}

	@Override
	public boolean changePassword(PasswordChangeDTO dto) {

		Member member = userRepository.findByUsername(dto.getUsername()).orElse(null);
		if (member == null)
			return false;

		if (!passwordEncoder.matches(dto.getOldPwd(), member.getUserPwd()))
			return false;

		member.setUserPwd(passwordEncoder.encode(dto.getNewPwd()));
		userRepository.save(member);
		return true;
	}

	@Override
	public boolean existsByUsername(String username) {
		return userRepository.findById(username).isPresent();
	}

}
