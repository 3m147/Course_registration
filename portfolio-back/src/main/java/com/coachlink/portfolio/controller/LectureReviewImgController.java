package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.entity.LectureReviewImg;
import com.coachlink.portfolio.service.LectureReviewImgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review-images")
public class LectureReviewImgController {

    @Autowired
    private LectureReviewImgService lectureReviewImgService;

    //이미지 등록
    @PostMapping("/{reviewId}")
    public ResponseEntity<LectureReviewImg> registerImg(
            @PathVariable Long reviewId,
            @RequestBody LectureReviewImg imgData) {

        LectureReviewImg savedImg = lectureReviewImgService.registerReviewImg(reviewId, imgData);

        if (savedImg == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(savedImg, HttpStatus.CREATED);
    }

    //특정 리뷰 이미지 목록 조회
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<LectureReviewImg>> getImgsByReview(@PathVariable Long reviewId) {

        List<LectureReviewImg> imgList = lectureReviewImgService.getImgsByReview(reviewId);

        if (imgList == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(imgList, HttpStatus.OK);
    }

    //이미지 이름으로 조회
    @GetMapping("/name/{imgName}")
    public ResponseEntity<LectureReviewImg> getImgByName(@PathVariable String imgName) {

        LectureReviewImg img = lectureReviewImgService.getImgByName(imgName);

        if (img == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(img, HttpStatus.OK);
    }

    //이미지 수정
    @PatchMapping("/{imgId}")
    public ResponseEntity<LectureReviewImg> updateImg(
            @PathVariable Long imgId,
            @RequestBody LectureReviewImg updatedData) {

        LectureReviewImg updatedImg = lectureReviewImgService.updateReviewImg(imgId, updatedData);

        if (updatedImg == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedImg, HttpStatus.OK);
    }

    //이미지 삭제
    @DeleteMapping("/{imgId}")
    public ResponseEntity<String> deleteImg(@PathVariable Long imgId) {

        boolean deleted = lectureReviewImgService.deleteReviewImg(imgId);

        if (!deleted) {
            return new ResponseEntity<>("이미지를 찾을 수 없습니다", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("이미지 삭제 완료", HttpStatus.OK);
    }
}
