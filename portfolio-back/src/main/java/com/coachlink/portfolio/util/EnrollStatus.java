package com.coachlink.portfolio.util;

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

public enum EnrollStatus {
	OPEN, // 수강신청가능
	OVERCAPACITY, // 인원초과
	ENROLLED, // 수강취소가능
	OWN_LECTURE, // 자기 강좌라서 수강신청할 수 없다
	ACTIVE_ENROLLED, // 수강신청되어 있고 확정되어 취소 불가
	ACTIVE_NOT_ENROLLED, // 수강신청 안되어있고 확정되어 수강 불가
	STARTED, // 시작되어 수강신청 불가
	FINISHED, // 종료되어 수강신청 불가
	CANCELLED // 취소됨
}
