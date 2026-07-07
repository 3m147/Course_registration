package com.coachlink.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.coachlink.portfolio.util.EnrollStatus;
import com.coachlink.portfolio.util.LectureStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureStateDTO {
	private long lectureId;
	private String lectureUsername;
	private String username;

	@Builder.Default
	private int minPeople = 0;
	@Builder.Default
	private int maxPeople = 0;
	@Builder.Default
	private int countEnrollment = 0;
	@Builder.Default
	private int alreadyEnrolled = 0;
	@Builder.Default
	private LocalDateTime startTime = LocalDateTime.now();
	@Builder.Default
	private LocalDateTime endTime = LocalDateTime.now();
	@Builder.Default
	private LectureStatus lectureStatus = LectureStatus.OPN;
	@Builder.Default
	private EnrollStatus enrollStatus = EnrollStatus.CANCELLED;

	public LectureStateDTO(Long lectureId, String username, Object[] list) {
		this.lectureId = lectureId;
		this.username = username;
		this.minPeople = Math.toIntExact((Long) list[0]);
		this.maxPeople = Math.toIntExact((Long) list[1]);
		this.countEnrollment = Math.toIntExact((Long) list[2]);
		this.alreadyEnrolled = (list[3] == null ? 0 : ((BigDecimal) list[3]).intValue()); // 넌 또 뭔데??????????
		this.startTime = (LocalDateTime) list[4];
		this.endTime = (LocalDateTime) list[5];
		this.lectureStatus = (LectureStatus) list[6];
		this.lectureUsername = (String) list[7];
		this.calculateState();
	}

	// 여기에 나올 상태가 버튼에 나올 상태이다.
	// 버튼에는 몇개의 상태가 나와야 하는가?
	/*
	 * 수강신청(가능)
	 * 인원초과 (신청 불가능)
	 * 수강취소(가능)
	 * 수강확정됨 (신청됨)
	 * 이미 시작된 강의입니다.
	 * 종료된 강의입니다.
	 * 취소된 강의입니다.
	 */

	// 확정 전: 오픈, 만원, 신청함, 내 강좌
	// 확정 후: 확정됨, 시작됨, 종료됨, 취소됨
	// 확정 후에는 신청도 취소도 안됨

	public void calculateState() {

		LocalDateTime now = LocalDateTime.now();
		// (now.isAfter(this.startTime.minusDays(3L))) {
		// 지금이 (시작 3일 전) 보다 뒤면 변경이 불가능하다
		if (this.lectureStatus == LectureStatus.CAN) {
			this.enrollStatus = EnrollStatus.CANCELLED; // 취소된 강의입니다.
		} else if (now.isAfter(this.endTime)) {
			this.enrollStatus = EnrollStatus.FINISHED; // 종료된 강의입니다.
		} else if (now.isAfter(this.startTime)) {
			this.enrollStatus = EnrollStatus.STARTED; // 시작된 강의입니다.
		} else if (now.isAfter(this.startTime.minusDays(3L))) {
			if (this.alreadyEnrolled > 0) {
				this.enrollStatus = EnrollStatus.ACTIVE_ENROLLED; // 수강신청 확정됨
			} else {
				this.enrollStatus = EnrollStatus.ACTIVE_NOT_ENROLLED;
			}
		} else { // 아니면 가능할수도 있다
			if (username.equals(lectureUsername)) {
				this.enrollStatus = EnrollStatus.OWN_LECTURE; // 자기 강좌에는 수강신청할 수 없다.
			} else {
				if (this.alreadyEnrolled > 0) {
					this.enrollStatus = EnrollStatus.ENROLLED; // 이미 신청함
				} else if (this.countEnrollment >= this.maxPeople) {
					this.enrollStatus = EnrollStatus.OVERCAPACITY; // 만원
				} else {
					this.enrollStatus = EnrollStatus.OPEN; // 신청가능
				}
			}
		}
	}
}
