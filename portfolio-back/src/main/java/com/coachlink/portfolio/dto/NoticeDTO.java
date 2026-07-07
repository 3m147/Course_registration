package com.coachlink.portfolio.dto;

import com.coachlink.portfolio.entity.Lecture;
import com.coachlink.portfolio.entity.Member;
import com.coachlink.portfolio.util.NoticeStatus;
import com.coachlink.portfolio.util.NoticeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {
    private Long noticeId;

    private String lectureName;

    private Long lectureId;

    private String member;

    private String noticeType;

    private String noticeContent;

    private String noticeStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
