package com.coachlink.portfolio.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LectureReviewImgDTO {
    
    private Long lectureReviewImgId;
    private Long reviewId;
    private String imgName;
    private String originalName;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
    private String base64;
    private String thumbnailName;
}
