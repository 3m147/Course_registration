package com.coachlink.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureImgDTO {
    private Long lectureImgId;
    private String originalName;
    private String contentType;
    private Long size;
    private String base64;
    private boolean isMainImg;

}
