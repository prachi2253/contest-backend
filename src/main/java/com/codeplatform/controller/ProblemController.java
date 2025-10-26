package com.codeplatform.controller;

import com.codeplatform.dto.ProblemResponse;
import com.codeplatform.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/{problemId}")
    public ResponseEntity<ProblemResponse> getProblemById(@PathVariable Long problemId) {
        try {
            ProblemResponse problem = problemService.getProblemById(problemId);
            return ResponseEntity.ok(problem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/contest/{contestId}")
    public ResponseEntity<List<ProblemResponse>> getProblemsByContestId(@PathVariable Long contestId) {
        List<ProblemResponse> problems = problemService.getProblemsByContestId(contestId);
        return ResponseEntity.ok(problems);
    }
}