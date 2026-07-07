package com.coachlink.portfolio.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.LectureEnrollmentDTO;
import com.coachlink.portfolio.dto.LectureStateDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PageResponseDTO;
import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.LectureEnrollment;
import com.coachlink.portfolio.entity.LectureImg;
import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.repository.LectureEnrollmentRepository;
import com.coachlink.portfolio.repository.LectureRepository;
import com.coachlink.portfolio.repository.UserRepository;
import com.coachlink.portfolio.util.EnrollStatus;
import com.coachlink.portfolio.util.LectureStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LectureEnrollmentServiceImpl implements LectureEnrollmentService {
	private final LectureEnrollmentRepository rep;
	private final UserRepository userRep;
	private final LectureRepository lectRep;
	private final LectureService lectServ;

	// CREATE
	// String은 유저 토큰이 될 것이다
	@Override
	@Transactional
	public LectureStateDTO create(Long lectureId, String username) throws Exception {

		// String result = null;

		// 1. 이미 있는지 체크한다
		LectureStateDTO dto = new LectureStateDTO(lectureId, username,
				rep.getLectureStateVariables(username, lectureId).get(0));

		if (dto.getEnrollStatus() == EnrollStatus.OPEN) {
			Member member = userRep.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			Lecture lecture = lectRep.findById(lectureId).get();
			LectureEnrollment le = LectureEnrollment.builder().lecture(lecture).member(member).build();
			le = rep.save(le);
			dto = checkState(username, lectureId);
		}

		return dto;
	}

	// DELETE
	@Override
	@Transactional
	public LectureStateDTO delete(Long lectureId, String username) throws Exception {

		LectureStateDTO dto = new LectureStateDTO(lectureId, username,
				rep.getLectureStateVariables(username, lectureId).get(0));

		// 수강취소가 가능한 유일한 상태이다.
		if (dto.getEnrollStatus() == EnrollStatus.ENROLLED) {
			rep.deleteLectureByIds(username, lectureId);
			dto = checkState(username, lectureId);
		}

		// dto에서 이미 다 state를 반환해주므로...

		return dto;
	}

	@Override
	@Transactional
	public LectureStateDTO checkState(String username, Long lectureId) throws Exception {
		LectureStateDTO dto = new LectureStateDTO(lectureId, username,
				rep.getLectureStateVariables(username, lectureId).get(0));

		System.out.println("checkState: " + dto.toString());

		// LocalDateTime start = dto.getStartTime();
		// LocalDateTime end = dto.getEndTime();
		// int minEnroll = dto.getMinPeople();
		// int currEnroll = dto.getCountEnrollment();
		// LectureStatus status = dto.getLectureStatus();

		// // status가 바뀌는 경우
		// // status는 4가지가 있다 - OPN, ACT, CAN, END
		// // END로 바뀌는 경우: 강의 종료 시점이 지났고, 강의 상태가 직전 CAN이 아니었던 경우
		// // ACT로 바뀌는 경우: 강의 상태가 OPN이었고 정원이 충족된경우
		// // CAN으로 바뀌는 경우: 강의 상태가 OPN이었고 정원이 충족안된경우

		// if (LocalDateTime.now().isAfter(end) && status != LectureStatus.CAN) {
		// status = LectureStatus.END;
		// dto.setLectureStatus(status);
		// dto.calculateState();
		// } else if (LocalDateTime.now().isAfter(start.minusDays(3))) {
		// if (currEnroll >= minEnroll) {
		// status = LectureStatus.ACT;
		// } else {
		// status = LectureStatus.CAN;
		// }
		// dto.setLectureStatus(status);
		// dto.calculateState();
		// }

		return dto;
	}

	@Override
	public void deleteByUser(String username) throws Exception {
		System.out.println("LectureEnrollmentService.deleteByUser() with username = " + username);
		rep.deleteByUser(username);
	}

	@Override
	public Map<String, Integer> countByLecture(Long lectureId) throws Exception {
		System.out.println("counting number of lecture enrollments of lecture id = " + lectureId);
		int[] counts = rep.countByLecture(lectureId);
		Map<String, Integer> map = new HashMap<>();
		int maxEnroll = counts[0];
		int currentEnroll = counts[1];
		map.put("maxEnroll", maxEnroll);
		map.put("currentEnroll", currentEnroll);
		return map;
	}

	// READ (by user)
	@Override
	@Transactional
	public PageResponseDTO<LectureDTO, Object[]> readByUser(PageRequestDTO prDTO, String username) throws Exception {
		System.out.println("LectureEnrollmentService.readByUser() with username = " + username);
		Pageable p = prDTO.getPageable("lecture.lectureStartTime", Sort.Direction.ASC);

		Page<Object[]> enrollments = rep.readByUser(p, username);

		enrollments.getContent().forEach(obj -> {
			System.out.println(((LectureEnrollment) obj[0]).getLecture());
			System.out.println((LectureImg) obj[1]);
		});

		Function<Object[], LectureDTO> fn = (arr -> lectServ.entityToDTO(
				((LectureEnrollment) arr[0]).getLecture(),
				(arr[1] != null) ? Arrays.asList((LectureImg) arr[1]) : null));

		return new PageResponseDTO<>(enrollments, fn);
	}

}
