package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.dto.LectureReviewDTO;
import com.coachlink.portfolio.entity.LectureReview;
import com.coachlink.portfolio.service.LectureReviewService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class LectureReviewController {

    private final LectureReviewService lectureReviewService;

    // 리뷰 작성
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> writeReview(
            @RequestParam("username") String username,
            @RequestParam("lectureId") Long lectureId,
            @RequestParam("rating") Integer rating,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files){


        LectureReview review = LectureReview.builder()
                .rating(rating)
                .content(content)
                .build();
        lectureReviewService.writeReview(username, lectureId, review, files);
        return new ResponseEntity<>("리뷰가 등록되었습니다.", HttpStatus.OK);
    }

    // 특정 강좌 리뷰 조회
    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<LectureReviewDTO>> getReviewsByLecture(@PathVariable Long lectureId) {

        List<LectureReview> reviewList = lectureReviewService.getReviewsByLecture(lectureId);

        List<LectureReviewDTO> dtoList = reviewList.stream()
                .map(r -> lectureReviewService.toDTO(r))
                .toList();

        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    // 리뷰 상세 조회
    @GetMapping("/detail/{reviewId}")
    public ResponseEntity<LectureReviewDTO> getReviewById(@PathVariable Long reviewId) {
        LectureReview review = lectureReviewService.getReviewById(reviewId);
        if (review != null) {
            return new ResponseEntity<>(lectureReviewService.toDTO(review), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // 리뷰 수정
    // 리뷰 수정
    @PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId,
            @RequestParam("rating") Integer rating,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deletedImgIds", required = false) List<Long> deletedImgIds) {

        LectureReview updatedData = LectureReview.builder()
                .rating(rating)
                .content(content)
                .build();

        lectureReviewService.updateReview(reviewId, updatedData, files, deletedImgIds);
        return new ResponseEntity<>("리뷰가 수정되었습니다.", HttpStatus.OK);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        lectureReviewService.deleteReview(reviewId);
        return new ResponseEntity<>("리뷰가 삭제되었습니다.", HttpStatus.OK);
    }
}
