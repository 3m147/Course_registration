package com.coachlink.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {
    private Long lectureId;
    private String username;
    private String facilityName;
    private String mainName;
    private String subName;
    private String lectureName;
    private String lectureContent;
    private String lectureStatus;
    private LocalDateTime lectureStartTime;
    private LocalDateTime lectureEndTime;
    private Long minPeople;
    private Long maxPeople;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double coordsX;
    private Double coordsY;

    @Builder.Default
    private List<String> images = new ArrayList<>();
    private List<LectureImgDTO> lecImgList;

    @Builder.Default
    private Integer mainIndex = 0;

    // @Builder.Default
    // private List<LectureReview> lecReviewList = new ArrayList<>();

    private Double avg;
    private Long reviewCnt;

    private double dist;
}
