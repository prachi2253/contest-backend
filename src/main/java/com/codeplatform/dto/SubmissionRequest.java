package com.codeplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private Long userId;
    private Long problemId;
    private Long contestId;
    private String code;
    private String language; // CPP, JAVA, PYTHON
}