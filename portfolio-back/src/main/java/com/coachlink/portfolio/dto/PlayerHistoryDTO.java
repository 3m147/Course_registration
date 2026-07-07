package com.coachlink.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHistoryDTO {
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
