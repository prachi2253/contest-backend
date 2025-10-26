package com.codeplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStatusResponse {
    private Long submissionId;
    private String status; // PENDING, RUNNING, ACCEPTED, WRONG_ANSWER, TLE, COMPILATION_ERROR, RUNTIME_ERROR
    private String result; // Detailed message
    private Integer executionTime; // in ms
    private LocalDateTime submittedAt;
}