package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.util.LectureStatus;
import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureRepository
        extends JpaRepository<Lecture, Long>, QuerydslPredicateExecutor<Lecture>, JpaSpecificationExecutor<Lecture> {

    @EntityGraph(attributePaths = { "member" }, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Lecture> findByLectureId(Long lectureId);

    @Query("select l, li from Lecture l " +
            "left outer join LectureImg li on li.lecture = l and li.isMainImg = true")
    Page<Object[]> getLectureList(Pageable pageable);

    @Query("select l, li from Lecture l " +
            "left outer join LectureImg li on li.lecture = l and li.isMainImg = true where l.member.username=:playerName")
    Page<Object[]> getPlayerLectureList(Pageable pageable, @Param("playerName") String playerName);

	// @Query("select l, li from Lecture l " +
	// "left outer join LectureImg li on li.lecture = l and li.isMainImg = true")
	// Page<Object[]> searchLectureList(BooleanBuilder bb, Pageable pageable);

    @Query("select l, li" +
            ", sqrt((l.coordsY - :userY)*(l.coordsY - :userY)*111.045*111.045 +	(l.coordsX - :userX)*(l.coordsX - :userX)*:factorX*:factorX) as dist"
            +
            " from Lecture l left outer join LectureImg li on (li.lecture = l and li.isMainImg = true)")
    Page<Object[]> searchLectureList2(Predicate bb, Pageable pageable, @Param("userX") Double userX,
            @Param("userY") Double userY, @Param("factorX") Double factorX);

    @Query(value = "select l.*, li.*" +
            ", sqrt((l.coordsy - ?2)*(l.coordsy - ?2)*111.045*111.045+(l.coordsx - ?1)*(l.coordsx - ?1)*?3*?3) as dist"
            +
            " from lectures l left outer join lecture_imgs li on (li.lecture_id = l.lecture_id and li.is_main_img = true)", nativeQuery = true)
    Page<Object[]> searchLectureList3(Pageable p, @Param("userX") Double userX,
            @Param("userY") Double userY, @Param("factorX") Double factorX);
    // https://stackoverflow.com/questions/62145715/jpa-query-with-predicate 이거였다.
    // 안되는게 맞았다.

    @Query("select l, li" +
            ", sqrt((l.coordsY - :userY)*(l.coordsY - :userY)*111.045*111.045 +	(l.coordsX - :userX)*(l.coordsX - :userX)*:factorX*:factorX) as dist"
            +
            " from Lecture l left outer join LectureImg li on (li.lecture = l and li.isMainImg = true)")
    Page<Object[]> searchLectureList4(Specification<Lecture> spec, Pageable pageable, @Param("userX") Double userX,
            @Param("userY") Double userY, @Param("factorX") Double factorX);

    @Query("select l, li from Lecture l " +
            "left outer join LectureImg li on li.lecture = l " +
            "where l.lectureId=:lectureId")
    List<Object[]> getLectureByLectureId(@Param("lectureId") Long lectureId);

    // @Query("UPDATE Lecture l SET l.lectureStatus=:lectureStatus WHERE
    // l.lectureId=:lectureId")
    // void updateStatusByLectureId(@Param("lectureId") Long lectureId,
    // @Param("lectureStatus") String lectureStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Lecture l SET l.lectureStatus = :lectureStatus WHERE l.lectureId = :lectureId")
    void updateStatusByLectureId(
            @Param("lectureId") Long lectureId,
            @Param("lectureStatus") LectureStatus lectureStatus);

    @Query("SELECT l, count(le.member.username) FROM Lecture l LEFT OUTER JOIN LectureEnrollment le ON le.lecture.lectureId = l.lectureId WHERE l.lectureStatus IN (LectureStatus.OPN, LectureStatus.ACT) GROUP BY l.lectureId")
    public List<Object[]> getUpdatedLectures();
}
