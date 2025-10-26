package com.codeplatform.controller;

import com.codeplatform.dto.LeaderboardResponse;
import com.codeplatform.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<LeaderboardResponse> getLeaderboard(@PathVariable Long contestId) {
        try {
            LeaderboardResponse leaderboard = leaderboardService.getLeaderboard(contestId);
            return ResponseEntity.ok(leaderboard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}