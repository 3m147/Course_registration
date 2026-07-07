package com.coachlink.portfolio.controller;

import com.coachlink.portfolio.entity.SportName;
import com.coachlink.portfolio.repository.SportNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportNameController {

    private final SportNameRepository sportNameRepository;

    @GetMapping
    public ResponseEntity<List<SportName>> getAllSportNames() {
        return ResponseEntity.ok(sportNameRepository.findAll());
    }
}
