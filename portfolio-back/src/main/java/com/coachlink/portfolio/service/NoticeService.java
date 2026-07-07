package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.NoticeDTO;
import com.coachlink.portfolio.dto.PageBlockDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PageResponseDTO;
import com.coachlink.portfolio.entity.Notice;

import java.util.Map;

public interface NoticeService {
    PageBlockDTO<NoticeDTO, Notice> getAllNoticesByUserId(String username, PageRequestDTO pageRequestDTO);

    default NoticeDTO entityToDTO(Notice notice) {
        return NoticeDTO.builder()
                .noticeId(notice.getNoticeId())
                .lectureName(notice.getLecture().getLectureName())
                .lectureId(notice.getLecture().getLectureId())
                .member(notice.getMember().getUsername())
                .noticeType(notice.getNoticeType().name())
                .noticeContent(notice.getNoticeContent())
                .noticeStatus(notice.getNoticeStatus().name())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    void readNoticeByLectureIdAndUserName(Long lectureId,String username);
}
