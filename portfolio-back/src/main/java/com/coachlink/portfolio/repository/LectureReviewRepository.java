package com.coachlink.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coachlink.portfolio.entity.LectureReview;

public interface LectureReviewRepository extends JpaRepository<LectureReview, Long>{

    // 전체 리뷰 
    @Query(value = "select * from lecture_reviews order by review_id desc", nativeQuery = true)
    List<LectureReview> findAllReviews();

    // 특정 강의 리뷰
    @Query(value = "select * from lecture_reviews where lecture_id = :lectureId order by review_id desc", nativeQuery = true)
    List<LectureReview> findByLectureId(@Param("lectureId") Long lectureId);

    // 유저 리뷰
    @Query(value = "select * from lecture_reviews where username = :username order by review_id desc", nativeQuery = true)
    List<LectureReview> findByUsername(@Param("username") String username);

    // 리뷰 상세
    @Query(value = "select * from lecture_reviews where review_id = :reviewId", nativeQuery = true)
    LectureReview findReviewDetail(@Param("reviewId") Long reviewId);

    // 강좌 평균 평점
    @Query(value = "select Round(avg(rating), 1) from lecture_reviews where lecture_id = :lectureId", nativeQuery = true)
    Double getAverageRating(@Param("lectureId") Long lectureId);
    
    // 강좌 리뷰 개수 
    @Query(value = "select count(*) from lecture_reviews where lecture_id = :lectureId", nativeQuery = true)
    int countByLecture(@Param("lectureId") Long lectureId);

    @Query("select count(lr), avg(lr.rating) from LectureReview lr where lr.lecture.id = :lectureId")
    List<Object[]> getLectureReviewStats(@Param("lectureId") Long lectureId);
}
