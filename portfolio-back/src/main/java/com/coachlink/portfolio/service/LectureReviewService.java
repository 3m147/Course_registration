package com.coachlink.portfolio.service;

import java.util.List;

import com.coachlink.portfolio.dto.LectureReviewDTO;
import com.coachlink.portfolio.entity.LectureReview;

public interface LectureReviewService {

    LectureReview writeReview(String username, Long lectureId, LectureReview reviewData, java.util.List<org.springframework.web.multipart.MultipartFile> files);

    List<LectureReview> getReviewsByLecture(Long lecutreId);
    
    LectureReview updateReview(Long reviewId, LectureReview updatedData, java.util.List<org.springframework.web.multipart.MultipartFile> files, List<Long> deletedImgIds);

    LectureReview getReviewById(Long reviewId);

    boolean deleteReview(Long reviewId);

    LectureReviewDTO toDTO(LectureReview review);
}
