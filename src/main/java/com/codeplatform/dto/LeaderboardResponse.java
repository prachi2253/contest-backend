package com.codeplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private Long contestId;
    private String contestTitle;
    private List<LeaderboardEntry> entries;
}