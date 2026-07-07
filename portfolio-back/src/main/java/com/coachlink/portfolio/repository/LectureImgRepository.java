package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.LectureImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LectureImgRepository extends JpaRepository<LectureImg, Long> {

    @Query("SELECT li FROM LectureImg li WHERE li.lecture.lectureId = :lectureId AND li.isMainImg = true")
    Optional<LectureImg> findByLectureIdAndIsMainImgTrue(@Param("lectureId") Long lectureId);

    @Modifying
    @Query("DELETE FROM LectureImg li WHERE li.lecture.lectureId=:lectureId")
    void deleteAllByLectureId(@Param("lectureId") Long lectureId);

}
