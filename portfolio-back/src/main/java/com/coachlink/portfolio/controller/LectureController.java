package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.PageBlockDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.security.jwt.JWTUtil;
import com.coachlink.portfolio.service.LectureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
@Slf4j
public class LectureController {
    private final LectureService lectureService;
    private final JWTUtil jwtUtil;

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> registerLecture(@RequestBody LectureDTO lectureDTO) {
        log.info("컨트롤러 호출" + lectureDTO.toString());
        Map<String, Object> json = new HashMap<>();
        try {
            // sportName 없으면 service에서 -1L 리턴
            Long lectureId = lectureService.registerLecture(lectureDTO);

            if (lectureId == -1L) {
                json.put("result", "fail");
                json.put("reason", "INVALID_SPORT_ID");
                json.put("message", "유효한 sportId가 필요합니다.");
                return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
            }

            json.put("result", "success");
            json.put("lectureId", lectureId);
            return new ResponseEntity<>(json, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                json.put("result", "fail");
                json.put("reason", "FORBIDDEN");
                json.put("message", e.getReason());
                return new ResponseEntity<>(json, HttpStatus.FORBIDDEN);
            }
            throw e;
        } catch (Exception e) {
            json.put("result", "fail");
            json.put("reason", "INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureDTO> getLectureByLectureId(@PathVariable Long lectureId) {
        LectureDTO lecture = lectureService.getLectureByLectureId(lectureId);

        return new ResponseEntity<>(lecture, HttpStatus.OK);
    }

    @PatchMapping("/{lectureId}/cancel")
    public void cancelByLectureId(
            @PathVariable Long lectureId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        lectureService.cancelByLectureId(lectureId, extractUsername(authorization));
    }

    @DeleteMapping("/{lectureId}")
    public void deleteByLectureId(
            @PathVariable Long lectureId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        lectureService.deleteByLectureId(lectureId, extractUsername(authorization));
    }

    @PutMapping(value = "/{lectureId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> updateLecture(@RequestBody LectureDTO lectureDTO) {
        Map<String, Object> json = new HashMap<>();
        try {
            Long lectureId = lectureService.updateLecture(lectureDTO);

            json.put("result", "success");
            json.put("lectureId", lectureId);
            return new ResponseEntity<>(json, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                json.put("result", "fail");
                json.put("reason", "FORBIDDEN");
                json.put("message", e.getReason());
                return new ResponseEntity<>(json, HttpStatus.FORBIDDEN);
            }
            throw e;
        } catch (Exception e) {
            json.put("result", "fail");
            json.put("reason", "INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 여기서 쿼리스트링 구조?
    // 필요한 것: 유저의 X / Y 좌표, 선택한 스포츠, 검색 스트링
    @GetMapping("")
    public ResponseEntity<PageBlockDTO<LectureDTO, Object[]>> getLectureList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) String sports,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String sortCol) {

        System.out.println("======================== LectureController.getLectureList ========================");
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(12).build();
        ResponseEntity<PageBlockDTO<LectureDTO, Object[]>> json = null;

        PageBlockDTO<LectureDTO, Object[]> result = null;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime startDate = (start == null || start.length() == 0) ? null
                : LocalDate.parse(start, format).atStartOfDay();
        LocalDateTime endDate = (end == null || end.length() == 0) ? null
                : LocalDate.parse(end, format).atTime(23, 59, 59);

        // System.out.println("Start = " + startDate);
        // System.out.println("end = " + endDate);

        if (sortCol == null) {
            sortCol = "lectureStartDate";
        }

        try {
            result = lectureService.searchLecture(pageRequestDTO, x, y, null, q, startDate, endDate, sortCol, page);
            json = new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            // result.put("message", e.getMessage());
            // result.put("result", "failed");
            json = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return json;
    }

    private String extractUsername(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        try {
            return jwtUtil.validateAndExtract(authorization.substring(7));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
    }

}
