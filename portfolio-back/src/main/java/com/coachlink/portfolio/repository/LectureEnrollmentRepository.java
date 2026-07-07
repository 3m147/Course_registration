package com.coachlink.portfolio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.query.Param;

import com.coachlink.portfolio.entity.LectureEnrollment;
import com.coachlink.portfolio.entity.Member;

public interface LectureEnrollmentRepository extends JpaRepository<LectureEnrollment, Long> {

	/**
	 * 사용자 PK인 username과 강의 PK인 lectureId로 수강신청한다.
	 * 
	 * @param username
	 * @param lectureId
	 */
	@Modifying
	@Query("INSERT into LectureEnrollment le (le.lecture, le.member) values ((SELECT l from Lecture l where l.lectureId = :lectureId), (SELECT m from Member m where m.username = :username))")
	public Long enrollByIds(@Param("username") String username, @Param("lectureId") Long lectureId);

	@Query("SELECT count(*) from LectureEnrollment le where le.member.username = :username and le.lecture.lectureId = :lectureId")
	public int checkEnroll(@Param("username") String username, @Param("lectureId") Long lectureId);

	@Modifying
	@Query("DELETE from LectureEnrollment le where le.member.username = :username and le.lecture.lectureId = :lectureId")
	public void deleteLectureByIds(@Param("username") String username, @Param("lectureId") Long lectureId);

	/**
	 * 유저에 딸린 수강내역을 모두 삭제한다
	 * 
	 * @param username
	 */
	@Modifying
	@Query("DELETE from LectureEnrollment le where le.member.username = :username")
	public void deleteByUser(@Param("username") String username);

	// user ID로 수강내역 찾기
	@Query("SELECT le, li from LectureEnrollment le left outer join LectureImg li on li.lecture = le.lecture where le.member.username = :username and (li.isMainImg = true or li.isMainImg is null)")
	@EntityGraph(attributePaths = { "lecture" }, type = EntityGraphType.FETCH)
	public Page<Object[]> readByUser(Pageable pageable, @Param("username") String username);

	// 강의에 딸린 수강내역 갯수 세기
	/**
	 * 
	 * @param lectureId
	 * @return int[] 배열을 반환한다.
	 *         [강좌의 최대 수강신청인원, 현재 강의에 수강신청한 사람의 수]
	 */
	@Query("SELECT l2.maxPeople, count(le.lectureEnrollmentId) from Lecture l2 left outer join LectureEnrollment le on le.lecture.lectureId = l2.lectureId where l2.lectureId = :lectureId")
	public int[] countByLecture(@Param("lectureId") Long lectureId);

	// 강의 수강내역 + 유저 수강 현황 + 강의 시작/종료일 한번에 가져오기
	/**
	 * @param username
	 * @param lectureId
	 * @return Object[] 배열을 반환한다.
	 *         [(강좌의 최소 수강신청인원), (강좌의 최대 수강신청인원),
	 *         (현재 강의에 수강신청한 사람의 수), (그 중 username으로 되어 있는 신청의 수),
	 *         (강의 시작일자), (강의 종료일자), (강의 상태), (강의 개설한 사람)]
	 */
	@Query("SELECT (l2.minPeople, l2.maxPeople, count(le.lectureEnrollmentId), sum(le.member.username = :username), l2.lectureStartTime, l2.lectureEndTime, l2.lectureStatus, l2.member.username) from Lecture l2 left outer join LectureEnrollment le on le.lecture.lectureId = l2.lectureId where l2.lectureId = :lectureId")
	public List<Object[]> getLectureStateVariables(@Param("username") String username,
			@Param("lectureId") Long lectureId);

	@Query("SELECT l2.maxPeople, count(le.lectureEnrollmentId), sum(le.member.username = :username) from Lecture l2 left outer join LectureEnrollment le on le.lecture.lectureId = l2.lectureId where l2.lectureId = :lectureId")
	public int[] countByLectureUser(@Param("username") String username, @Param("lectureId") Long lectureId);


  @Query("SELECT le.member.username from LectureEnrollment le where le.lecture.lectureId = :lectureId")
  public List<String> findAllByLectureId(@Param("lectureId") Long lectureId);

  @Modifying
  @Query("DELETE FROM LectureEnrollment le WHERE le.lecture.lectureId = :lectureId")
  void deleteAllByLectureId(@Param("lectureId") Long lectureId);

	void deleteByMember(Member member);
}
