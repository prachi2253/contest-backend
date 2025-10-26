package com.codeplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    private Integer rank;
    private Long userId;
    private String userName;
    private Integer problemsSolved;
    private Long timeToFirstAC; // Time in seconds from contest start to first AC
}