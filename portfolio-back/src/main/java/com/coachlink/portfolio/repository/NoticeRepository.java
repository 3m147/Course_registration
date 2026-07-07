package com.coachlink.portfolio.repository;

import com.coachlink.portfolio.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE n.member.username=:username")
    Page<Notice> findByUsername(@Param("username") String username, Pageable pageable);

    @Modifying
    @Query("UPDATE Notice n SET n.noticeStatus = READ WHERE n.lecture.id = :lectureId AND n.member.username = :username")
    void readNoticeByLectureIdAndUserName(@Param("lectureId") Long lectureId,
                                         @Param("username") String username);

    @Query("SELECT COUNT(n) FROM Notice n WHERE n.member.username = :username AND n.noticeStatus = NoticeStatus.NEW")
    long newNoticeByUsername(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM Notice n WHERE n.lecture.lectureId = :lectureId")
    void deleteAllByLectureId(@Param("lectureId") Long lectureId);
}
