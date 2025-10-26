package com.codeplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<SampleTestCase> sampleTestCases;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SampleTestCase {
        private String input;
        private String output;
    }
}