package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.NoticeDTO;
import com.coachlink.portfolio.dto.PageBlockDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PageResponseDTO;
import com.coachlink.portfolio.entity.Notice;
import com.coachlink.portfolio.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public PageBlockDTO<NoticeDTO, Notice> getAllNoticesByUserId(
            String username,
            PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("createdAt", Sort.Direction.DESC);
        Page<Notice> noticePage = noticeRepository.findByUsername(username, pageable);
        long newNotice = noticeRepository.newNoticeByUsername(username);

        Function<Notice, NoticeDTO> fn = notice -> entityToDTO(notice);

        return new PageBlockDTO<>(noticePage, fn, newNotice);
    }


    @Override
    @Transactional
    public void readNoticeByLectureIdAndUserName(Long lectureId, String username) {
        noticeRepository.readNoticeByLectureIdAndUserName(lectureId, username);
    }

}
