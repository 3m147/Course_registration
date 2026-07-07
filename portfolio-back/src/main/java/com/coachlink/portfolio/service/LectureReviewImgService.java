package com.coachlink.portfolio.service;

import java.util.List;
import com.coachlink.portfolio.entity.LectureReviewImg;

public interface LectureReviewImgService {

    LectureReviewImg registerReviewImg(Long reviewId, LectureReviewImg imgData);
    List<LectureReviewImg> getImgsByReview(Long reviewId);
    LectureReviewImg getImgByName(String imgName);
    LectureReviewImg updateReviewImg(Long imgId, LectureReviewImg updatedData);
    boolean deleteReviewImg(Long imgId);
}
