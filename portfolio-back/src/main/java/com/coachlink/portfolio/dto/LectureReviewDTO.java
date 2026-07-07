package com.coachlink.portfolio.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureReviewDTO {

    private Long reviewId;
    private String username;
    private Long lectureId;
    private int rating;
    private String content;
    private String reviewStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private java.util.List<LectureReviewImgDTO> reviewImages;

}
