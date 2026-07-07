package com.coachlink.portfolio.service;

import com.coachlink.portfolio.dto.*;
import com.coachlink.portfolio.entity.*;
import com.coachlink.portfolio.repository.*;
import com.coachlink.portfolio.util.LectureStatus;
import com.coachlink.portfolio.util.NoticeType;
import com.coachlink.portfolio.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureServiceImpl implements LectureService {
    private final LectureRepository lectureRepository;
    private final LectureReviewRepository lectureReviewRepository;
    private final LectureReviewImgRepository lectureReviewImgRepository;
    private final LectureImgRepository lectureImgRepository;
    private final SportNameRepository sportNameRepository;
    private final UserRepository userRepository;
    private final LectureEnrollmentRepository lectureEnrollmentRepository;
    private final NoticeRepository noticeRepository;

    private final LectureSearchRepositoryImpl lectureSearchRepositoryImpl;

    // 통합검색
    @Override
    public PageBlockDTO<LectureDTO, Object[]> searchLecture(
            PageRequestDTO pageRequestDTO,
            Double userX, Double userY,
            List<Long> sports,
            String query,
            LocalDateTime startTimeRange, LocalDateTime endTimeRange,
            String sort, int page) {

        List<String> queries = new ArrayList<>();
        if (query != null && query.length() > 0) {
            // 검색어 처리
            // List로 자른다
            queries = Arrays.stream(query.split("\\s+"))
                    .filter(str -> !str.contains(";") || !(str.length() < 2 && str.charAt(0) < 127)).toList();
            // ;가 포함된 검색어 걸러내기
            // 영어 / 숫자 1글자로 된 검색어 걸러내기
        }

        Page<Object[]> queryResult = lectureSearchRepositoryImpl.searchLecture(
                pageRequestDTO,
                userX, userY,
                sports, queries,
                startTimeRange, endTimeRange,
                sort);

        // 문제: searchResult는 page가 아니다
        // 앞단을 바꿔도 되고,
        // PageResponseDTO에는 어차피 List 하나만 들어있기 때문에 페이지 객체에 담아서 주지않아도 된다?
        // 근데 이러면 나중에 현업에서

        List<LectureDTO> result = new ArrayList<>();
        for (Object arr[] : queryResult) {
            LectureDTO dto = entityToDTO(
                    (Lecture) arr[0],
                    (arr[1] != null) ? Arrays.asList((LectureImg) arr[1]) : null);
            dto.setDist((Double) arr[2]);
            result.add(dto);
        }

        Function<Object[], LectureDTO> fn = (arr -> {
            LectureDTO dto = entityToDTO(
                    (Lecture) arr[0],
                    (arr[1] != null) ? Arrays.asList((LectureImg) arr[1]) : null);
            dto.setDist((Double) arr[2]);
            return dto;
        });
        long newnotice = 0;

        return new PageBlockDTO<>(queryResult, fn, newnotice);

    }

    @Override
    @Transactional
    public Long registerLecture(LectureDTO lectureDTO) throws Exception {
        Map<String, Object> entityMap = dtoToEntity(lectureDTO);
        Lecture lecture = (Lecture) entityMap.get("lecture");
        List<LectureImg> lectureImgList = (List<LectureImg>) entityMap.get("lectureImgList");

        Member member = userRepository.findByUsername(lectureDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        if (!member.getRoleSet().contains(Role.PLAYER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "강좌 등록 권한이 없습니다.");
        }
        lecture.setMember(member);
        Optional<SportName> sportName = sportNameRepository.findBySubName(lectureDTO.getSubName());

        if (sportName.isPresent()) {
            lecture.setSportName(sportName.get());
        } else {
            return -1L;
        }

        Lecture savedLecture = lectureRepository.save(lecture);
        log.info("=========저장된 Lecture====={}", savedLecture);

        // 6) 이미지가 있으면 함께 저장
        if (lectureImgList != null && !lectureImgList.isEmpty()) {
            for (LectureImg lectureImg : lectureImgList) {
                if (lectureImg.getLecture() == null) {
                    lectureImg.setLecture(savedLecture);
                }
                lectureImgRepository.save(lectureImg);
                log.info("LectureImg 저장: {}", lectureImg);
            }
        }

        return lecture.getLectureId();
    }

    @Override
    @Transactional
    public Long updateLecture(LectureDTO lectureDTO) {
        Map<String, Object> entityMap = dtoToEntity(lectureDTO);
        Lecture lecture = (Lecture) entityMap.get("lecture");
        List<LectureImg> lectureImgList = (List<LectureImg>) entityMap.get("lectureImgList");
        Long lectureId = lecture.getLectureId();
        List<String> enrollmentList = lectureEnrollmentRepository.findAllByLectureId(lectureId);
        for (String enrolledUsername : enrollmentList) {
            Member member = userRepository.findByUsername(enrolledUsername)
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다." + enrolledUsername));
            noticeRepository.save(
                    Notice.builder()
                            .lecture(lecture)
                            .member(member)
                            .noticeContent("강좌 정보가 수정되었습니다.")
                            .noticeType(NoticeType.UPDATED)
                            .build());
        }

        Member member = userRepository.findByUsername(lectureDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        if (!member.getRoleSet().contains(Role.PLAYER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "강좌 수정 권한이 없습니다.");
        }
        lecture.setMember(member);
        SportName sportName = sportNameRepository.findBySubName(lectureDTO.getSubName())
                .orElseThrow(() -> new IllegalStateException("강좌를 찾을 수 없습니다."));
        lecture.setSportName(sportName);

        lectureRepository.save(lecture);
        System.out.println("=========수정할=====" + lecture.toString());

        lectureImgRepository.deleteAllByLectureId(lectureId);

        if (lectureImgList != null && !lectureImgList.isEmpty()) {
            lectureImgList.forEach(lectureImg -> {
                lectureImg.setLecture(lecture);
                lectureImgRepository.save(lectureImg);
                log.info(lectureImg.toString());
            });
        }
        return lectureId;
    }

    @Override
    @Transactional
    public void cancelByLectureId(Long lectureId, String username) {
        Lecture lecture = getOwnedLecture(lectureId, username);

        if (lecture.getLectureStatus() == LectureStatus.CAN) {
            return;
        }

        if (lecture.getLectureStatus() == LectureStatus.END) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "종료된 강좌는 취소할 수 없습니다.");
        }

        List<String> enrollmentList = lectureEnrollmentRepository.findAllByLectureId(lectureId);
        for (String enrolledUsername : enrollmentList) {
            Member member = userRepository.findByUsername(enrolledUsername)
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다." + enrolledUsername));
            noticeRepository.save(
                    Notice.builder()
                            .lecture(lecture)
                            .member(member)
                            .noticeContent("강좌가 취소되었습니다.")
                            .noticeType(NoticeType.DELETED)
                            .build());
        }
        lectureRepository.updateStatusByLectureId(lectureId, LectureStatus.CAN);
    }

    @Override
    @Transactional
    public void deleteByLectureId(Long lectureId, String username) {
        Lecture lecture = getOwnedLecture(lectureId, username);
        List<LectureReview> reviews = lectureReviewRepository.findByLectureId(lectureId);

        reviews.forEach(lectureReviewImgRepository::deleteByLectureReview);
        lectureReviewRepository.deleteAll(reviews);
        noticeRepository.deleteAllByLectureId(lectureId);
        lectureEnrollmentRepository.deleteAllByLectureId(lectureId);
        lectureImgRepository.deleteAllByLectureId(lectureId);
        lectureRepository.delete(lecture);
    }

    private Lecture getOwnedLecture(Long lectureId, String username) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId)
                .orElseThrow(() -> new IllegalStateException("강좌를 찾을 수 없습니다."));

        if (!lecture.getMember().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인이 등록한 강좌만 관리할 수 있습니다.");
        }

        return lecture;
    }

    @Override
    public PageBlockDTO<LectureDTO, Object[]> getLectureList(PageRequestDTO pageRequestDTO) throws Exception {
        Pageable pageable = pageRequestDTO.getPageable("lectureStartTime", Sort.Direction.ASC);
        Page<Object[]> result = lectureRepository.getLectureList(pageable);
        Function<Object[], LectureDTO> fn = (arr -> entityToDTO(
                (Lecture) arr[0],
                (arr[1] != null) ? Arrays.asList((LectureImg) arr[1]) : null));
        long newnotice = 0;
        return new PageBlockDTO<>(result, fn, newnotice);
    }

    @Override
    public LectureDTO getLectureByLectureId(Long lectureId) {
        // 강좌 상세정보
        List<Object[]> rows = lectureRepository.getLectureByLectureId(lectureId);

        if (rows == null || rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강좌를 찾을 수 없습니다.");
        }

        // 리뷰 정보
        List<Object[]> reviewStatsList = lectureReviewRepository.getLectureReviewStats(lectureId);

        Long reviewCnt = 0L;
        Double avg = null;

        if (reviewStatsList != null && !reviewStatsList.isEmpty()) {
            Object[] stats = reviewStatsList.get(0);
            if (stats[0] != null)
                reviewCnt = ((Number) stats[0]).longValue();
            if (stats[1] != null)
                avg = ((Number) stats[1]).doubleValue();
        }

        LectureDTO lectureDTO = null;

        for (Object[] arr : rows) {
            Lecture lecture = (Lecture) arr[0];
            LectureImg li = (LectureImg) arr[1];

            if (lectureDTO == null) {
                lectureDTO = entityToDTO(lecture, new ArrayList<>());
                lectureDTO.setReviewCnt(reviewCnt);
                lectureDTO.setAvg(avg);
            }

            if (li != null) {
                LectureImgDTO liDto = LectureImgDTO.builder()
                        .lectureImgId(li.getLectureImgId())
                        .originalName(li.getOriginalName())
                        .contentType(li.getContentType())
                        .size(li.getSize())
                        .base64(li.getBase64())
                        .build();

                lectureDTO.getLecImgList().add(liDto);
            }
        }

        return lectureDTO;
    }

    // 강의 상태 바꾸기
    @Override
    public void changeLectureState(Long lectureId, LectureStatus status) throws Exception {
        lectureRepository.updateStatusByLectureId(lectureId, status);
    }

    @Override
    public PageBlockDTO<LectureDTO, Object[]> getPlayerLectureList(PageRequestDTO pageRequestDTO, String playerName) {
        Pageable pageable = pageRequestDTO.getPageable("lectureStartTime", Sort.Direction.ASC);
        Page<Object[]> result = lectureRepository.getPlayerLectureList(pageable, playerName);
        Function<Object[], LectureDTO> fn = (arr -> entityToDTO(
                (Lecture) arr[0],
                (arr[1] != null) ? Arrays.asList((LectureImg) arr[1]) : null));
        long newnotice = 0;
        return new PageBlockDTO<>(result, fn, newnotice);
    }

}
