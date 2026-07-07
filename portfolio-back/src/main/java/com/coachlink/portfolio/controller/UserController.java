package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.dto.MemberDTO;
import com.coachlink.portfolio.dto.PasswordChangeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.service.UserService;

import lombok.RequiredArgsConstructor;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody MemberDTO memberDTO) {

		Member newMember = userService.registerUser(memberDTO);

		if (newMember == null) {
			return new ResponseEntity<>("이미 존재하는 회원입니다", HttpStatus.CONFLICT);
		}

		return new ResponseEntity<>("회원가입이 완료되었습니다", HttpStatus.CREATED);
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getUser(@PathVariable String username) {
		Member member = userService.getUserDetail(username);

		if (member == null) {
			return new ResponseEntity<>("회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(member, HttpStatus.OK);
	}

	@PutMapping("/{username}")
	public ResponseEntity<String> updateUser(
			@PathVariable String username,
			@RequestBody Member updatedMemberData) {

		Member result = userService.updateUser(username, updatedMemberData);

		if (result == null) {
			return new ResponseEntity<>("회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("회원 정보가 수정되었습니다", HttpStatus.OK);
	}

	@DeleteMapping("/{username}")
	public ResponseEntity<String> deleteUser(@PathVariable String username) {
		boolean deleted = userService.deleteUser(username);

		if (!deleted) {
			return new ResponseEntity<>("존재하지 않는 회원입니다", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("회원이 탈퇴 되었습니다", HttpStatus.OK);
	}

	@PutMapping("/password-change")
	public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO dto) {
		boolean success = userService.changePassword(dto);
		if (success) {
			return ResponseEntity.ok("비밀번호가 변경되었습니다");
		} else {
			return ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않습니다");
		}
	}

	@GetMapping("/checkId")
	public ResponseEntity<Boolean> checkId(@RequestParam String username) {

		boolean exists = userService.existsByUsername(username);

		return ResponseEntity.ok(!exists);
	}
}
