package com.codeplatform.controller;

import com.codeplatform.dto.SubmissionRequest;
import com.codeplatform.dto.SubmissionResponse;
import com.codeplatform.dto.SubmissionStatusResponse;
import com.codeplatform.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submitCode(@RequestBody SubmissionRequest request) {
        try {
            SubmissionResponse response = submissionService.submitCode(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SubmissionResponse(null, "ERROR", e.getMessage()));
        }
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionStatusResponse> getSubmissionStatus(@PathVariable Long submissionId) {
        try {
            SubmissionStatusResponse response = submissionService.getSubmissionStatus(submissionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}