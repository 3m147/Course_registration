package com.coachlink.portfolio.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.LectureStateDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PageResponseDTO;
import com.coachlink.portfolio.service.LectureEnrollmentService;
import com.coachlink.portfolio.util.EnrollStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(value = "/enroll")
@RequiredArgsConstructor
public class LectureEnrollmentController {

	private final LectureEnrollmentService service;

	// JSON으로 넘어온 Lecture와 User 정보를 받는다
	// 왜냐하면 Lecture 정보와 User 정보는 이미 앞단에 있을 것이기에...!
	@PostMapping("/{lectureId}")
	@PreAuthorize("hasRole('MEMBER')")
	public ResponseEntity<Object> create(@PathVariable Long lectureId,
			@AuthenticationPrincipal String username) {
		ResponseEntity<Object> result = null;

		// 받아야 할것: 유저, 강의
		try {
			System.out.println("user " + username + " enrolls into lecture number " + lectureId);
			LectureStateDTO reply = service.create(lectureId, username);
			if (reply.getEnrollStatus() == EnrollStatus.OPEN) {
				result = new ResponseEntity<>(reply, HttpStatus.OK);
			} else {
				result = new ResponseEntity<>(reply, HttpStatus.CONFLICT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	@DeleteMapping("/{lectureId}")
	@PreAuthorize("hasRole('MEMBER')")
	public ResponseEntity<Object> delete(@PathVariable Long lectureId,
			@AuthenticationPrincipal String username) {
		ResponseEntity<Object> result = null;

		// 받아야 할것: 유저, 강의
		try {
			LectureStateDTO reply = service.delete(lectureId, username);
			if (reply.getEnrollStatus() == EnrollStatus.ENROLLED) {
				result = new ResponseEntity<>(reply, HttpStatus.OK);
			} else {
				result = new ResponseEntity<>(reply, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	// 유저가 수강신청을 할 수 있는지 한번에 보는 방법
	@GetMapping("/{lectureId}")
	@PreAuthorize("hasRole('MEMBER')")
	public ResponseEntity<Object> checkEnroll(@PathVariable Long lectureId,
			@AuthenticationPrincipal String username) {

		ResponseEntity<Object> result = null;
		try {
			System.out.println("username: " + username + " / lectureId = " + lectureId);
			LectureStateDTO data = service.checkState(username, lectureId);
			System.out.println(data.toString());
			result = new ResponseEntity<>(data, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	// // 유저의 탈퇴로 인해 수강신청이 모두 삭제되는 경우
	// @DeleteMapping("")
	// @PreAuthorize("hasRole('MEMBER')")
	// public ResponseEntity<Map<String, Object>>
	// deleteByUser(@PathVariable("username") String username) {
	// ResponseEntity<Map<String, Object>> result = null;

	// System.out.println("LectureEnrollmentController: Deleting enrollment by
	// username = " + username);

	// try {

	// } catch (Exception e) {
	// }

	// return result;
	// }

	// 로그인한 유저의 모든 수강신청내역
	@GetMapping("/member")
	public ResponseEntity<Object> getEnrolledList(
			@AuthenticationPrincipal String username) {

		PageRequestDTO prDTO = PageRequestDTO.builder().page(1).size(10).build();
		ResponseEntity<Object> response = null;

		try {
			PageResponseDTO<LectureDTO, Object[]> page = service.readByUser(prDTO, username);
			response = new ResponseEntity<>(page, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	// 수강신청 UPDATE는 할필요 없다.
	// 수강신청 READ - 2가지 다 필요하다.
	// 강의에 딸린 모든 수강신청 가져오기, 유저에 딸린 모든 수강신청 가져오기

	// 강의 ID로 수강신청 갯수 가져오기
	/**
	 * @param lectureId
	 * @return
	 */
	public ResponseEntity<Object> countByLecture(Long lectureId) {
		ResponseEntity<Object> response = null;
		try {
			Map<String, Integer> map = service.countByLecture(lectureId);
			response = new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	public ResponseEntity<Object> readByUser(String username) {
		ResponseEntity<Object> response = null;

		return response;
	}

}
