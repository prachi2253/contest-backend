package com.codeplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ProblemSummary> problems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemSummary {
        private Long id;
        private String title;
        private String difficulty;
    }
}