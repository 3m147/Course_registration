package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.dto.LectureDTO;
import com.coachlink.portfolio.dto.PageBlockDTO;
import com.coachlink.portfolio.dto.PageRequestDTO;
import com.coachlink.portfolio.dto.PlayerHistoryDTO;
import com.coachlink.portfolio.service.LectureService;
import com.coachlink.portfolio.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
@Slf4j
public class PlayerController {

    private final LectureService lectureService;
    private final PlayerService playerService;

    @GetMapping("/lectures/{playerName}")
    public ResponseEntity<PageBlockDTO<LectureDTO, Object[]>> getPlayerLectureList(
            @RequestParam(defaultValue = "1") int page,
            @PathVariable String playerName) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(12).build();
        ResponseEntity<PageBlockDTO<LectureDTO, Object[]>> json = null;

        PageBlockDTO<LectureDTO, Object[]> result = null;
        try {
            result = lectureService.getPlayerLectureList(pageRequestDTO, playerName);
            json = new ResponseEntity<>(result, HttpStatus.OK);
//            log.info("player lecture: "+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            json = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return json;
    }

    @GetMapping("/histories/{playerName}")
    public ResponseEntity<List<PlayerHistoryDTO>> getPlayerHistoryList(@PathVariable String playerName) {
        try {
            List<PlayerHistoryDTO> result = playerService.getPlayerHistoryList(playerName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
