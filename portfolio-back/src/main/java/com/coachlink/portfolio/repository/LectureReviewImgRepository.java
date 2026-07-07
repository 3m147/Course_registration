package com.coachlink.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coachlink.portfolio.entity.LectureReviewImg;

public interface LectureReviewImgRepository extends JpaRepository<LectureReviewImg, Long> {
    
    @Query(value = "select * from lecture_reviews_imgs order by lecture_review_img_id desc", nativeQuery = true)
    List<LectureReviewImg> findAllImgs();

    @Query(value = "select * from lecture_reviews_imgs where review_id = :reviewId order by lecture_review_img_id asc", nativeQuery = true)
    List<LectureReviewImg> findByReviewId(@Param("reviewId") Long reviewId);

    @Query(value = "select * from lecture_reviews_imgs where img_name = :imgName", nativeQuery = true)
    LectureReviewImg findByImgName(@Param("imgName") String imgName);

    @Query(value = "select count(*) from lecture_reviews_imgs where review_id = :reviewId", nativeQuery = true)
    int countByReview(@Param("reviewId") Long reviewId);

    @Query(value = "select count(*) from lecture_reviews_imgs where img_name = :imgName", nativeQuery = true)
    Integer existsByImgName(@Param("imgName") String imgName);

    void deleteByLectureReview(com.coachlink.portfolio.entity.LectureReview lectureReview);
}
