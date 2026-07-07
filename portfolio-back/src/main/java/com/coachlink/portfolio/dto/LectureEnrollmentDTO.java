package com.coachlink.portfolio.dto;

import java.time.LocalDateTime;

import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureEnrollmentDTO {
	private Long lectureEnrollmentId;
	private Member member;
	private Lecture lecture;
	private LocalDateTime createdAt;
}
