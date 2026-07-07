package com.coachlink.portfolio.service;

import java.util.Map;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.LectureEnrollmentDTO;
import com.coachlink.portfolio.dto.LectureStateDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PageResponseDTO;
import com.coachlink.portfolio.entity.LectureEnrollment;

public interface LectureEnrollmentService {

	// default LectureEnrollment dtoToEntity(LectureEnrollmentDTO dto) {
	// return LectureEnrollment.builder()
	// .user(dto.getUser())
	// .lecture(dto.getLecture())
	// .build();
	// }

	default LectureEnrollmentDTO entityToDTO(LectureEnrollment en) {
		return LectureEnrollmentDTO.builder()
				// .user(en.getUser())
				// .lecture(en.getLecture())
				.lectureEnrollmentId(en.getLectureEnrollmentId())
				.createdAt(en.getCreatedAt())
				.build();
	}

	LectureStateDTO create(Long lectureId, String username) throws Exception;

	LectureStateDTO delete(Long lectureId, String username) throws Exception;

	PageResponseDTO<LectureDTO, Object[]> readByUser(PageRequestDTO prDTO, String username) throws Exception;

	void deleteByUser(String username) throws Exception;

	// List<LectureEnrollmentDTO> readByLecture(Long lectureId) throws Exception;
	/**
	 * 
	 * @param lectureId 강좌 ID
	 * @return [maxEnroll, currentEnroll]
	 * @throws Exception
	 */
	Map<String, Integer> countByLecture(Long lectureId) throws Exception;

	/**
	 * @param username  유저 이름
	 * @param lectureId 강좌 ID
	 * @return LectureStateDTO
	 * @throws Exception
	 */
	LectureStateDTO checkState(String username, Long lectureId) throws Exception;
}
