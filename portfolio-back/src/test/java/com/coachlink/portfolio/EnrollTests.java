package com.coachlink.portfolio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.LectureStateDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.repository.LectureEnrollmentRepository;
import com.coachlink.portfolio.service.LectureEnrollmentService;
import com.coachlink.portfolio.util.LectureStatus;

import jakarta.transaction.Transactional;

@SpringBootTest
public class EnrollTests {

	@Autowired
	LectureEnrollmentRepository leRepo;

	@Autowired
	LectureEnrollmentService leServ;

	// @Test
	// public void lectureState() {
	// String username = "test002";
	// Long lecture = 3L;
	// System.out.println(Arrays.toString(leRepo.countByLectureUser(username,
	// lecture)));
	// }

	// @Test
	// public void lectureState2() {
	// String username = "test002";
	// Long lecture = 3L;
	// Object[] arr = leRepo.getLectureStateVariables(username, lecture);
	// for (Object o : arr) {
	// System.out.println(o.toString());
	// }
	// }

	// @Test
	// public void lectureState3() {
	// String username = "test002";
	// Long lecture = 3L;
	// Object obj = leRepo.getLectureStateVariables(username, lecture).get(0);
	// LectureStateDTO dto = (LectureStateDTO) obj;
	// System.out.println(dto);
	// }

	@Test
	public void lectureState4() {
		String username = "test002";
		Long lecture = 3L;
		System.out.println(Arrays.toString(leRepo.getLectureStateVariables(username, lecture).get(0)));
	}

	// @Test
	// public void lectureType1() {
	// String username = "test002";
	// Long lecture = 3L;
	// LocalDateTime st = leRepo.lectureSelectTest2(lecture);
	// System.out.println(st);
	// }

	@Test
	@Transactional
	void enrollList() {
		String username = "test014";

		PageRequestDTO prDTO = PageRequestDTO.builder().page(1).size(10).build();

		try {
			List<LectureDTO> list = leServ.readByUser(prDTO, username).getDtoList();
		} catch (Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

}
