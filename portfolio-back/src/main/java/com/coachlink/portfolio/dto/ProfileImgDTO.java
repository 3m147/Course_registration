package com.coachlink.portfolio.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImgDTO {

    private Long profileImgId;
    private String username;
    private String imgName;
    private String contentType;
    private Long size;
    private String base64;
    private String thumbnailName;
    private LocalDate createdAt;
}
