package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.dto.NoticeDTO;
import com.coachlink.portfolio.dto.PageBlockDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PageResponseDTO;
import com.coachlink.portfolio.entity.Notice;
import com.coachlink.portfolio.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member/me/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/{username}/{pageNo}")
    public ResponseEntity<PageBlockDTO<NoticeDTO, Notice>> getNoticeList(@PathVariable("username") String username
    , @PathVariable("pageNo") int pageNo) {
        log.info("공지컨트롤러호출");
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(pageNo).size(20).build();
        ResponseEntity<PageBlockDTO<NoticeDTO, Notice>> json = null;

        PageBlockDTO<NoticeDTO, Notice> result = null;
        try {
            result = noticeService.getAllNoticesByUserId(username, pageRequestDTO);
            json = new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return json;
    }

    @PutMapping("/{lectureId}")
    public void readNotice(@PathVariable("lectureId") Long lectureId, Authentication authentication) {
        String username = authentication.getName();
        log.info("현재 username" + username);
        try {
            noticeService.readNoticeByLectureIdAndUserName(lectureId, username);
            log.info("성공");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("실패");
        }
    }
}