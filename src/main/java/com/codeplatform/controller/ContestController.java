package com.codeplatform.controller;

import com.codeplatform.dto.ContestResponse;
import com.codeplatform.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContestController {

    private final ContestService contestService;

    @GetMapping
    public ResponseEntity<List<ContestResponse>> getAllContests() {
        List<ContestResponse> contests = contestService.getAllContests();
        return ResponseEntity.ok(contests);
    }

    @GetMapping("/{contestId}")
    public ResponseEntity<ContestResponse> getContestById(@PathVariable Long contestId) {
        try {
            ContestResponse contest = contestService.getContestById(contestId);
            return ResponseEntity.ok(contest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<ContestResponse>> getActiveContests() {
        List<ContestResponse> contests = contestService.getActiveContests();
        return ResponseEntity.ok(contests);
    }
}