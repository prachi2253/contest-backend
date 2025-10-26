package com.codeplatform.service;

import com.codeplatform.dto.LeaderboardEntry;
import com.codeplatform.dto.LeaderboardResponse;
import com.codeplatform.entity.Contest;
import com.codeplatform.entity.Submission;
import com.codeplatform.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final SubmissionRepository submissionRepository;
    private final ContestService contestService;

    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderboard(Long contestId) {
        Contest contest = contestService.getContestEntityById(contestId);

        // Get all accepted submissions for this contest
        List<Submission> acceptedSubmissions = submissionRepository
                .findAcceptedSubmissionsByContestId(contestId);

        // Group by user and calculate stats
        Map<Long, UserStats> userStatsMap = new HashMap<>();

        for (Submission submission : acceptedSubmissions) {
            Long userId = submission.getUser().getId();
            Long problemId = submission.getProblem().getId();

            userStatsMap.putIfAbsent(userId, new UserStats(
                    userId,
                    submission.getUser().getName()
            ));

            UserStats stats = userStatsMap.get(userId);

            // Only count first AC for each problem
            if (!stats.solvedProblems.contains(problemId)) {
                stats.solvedProblems.add(problemId);

                // Calculate time from contest start to this submission
                long secondsToSolve = Duration.between(
                        contest.getStartTime(),
                        submission.getSubmittedAt()
                ).getSeconds();

                // Track earliest AC time
                if (stats.firstACTime == null || secondsToSolve < stats.firstACTime) {
                    stats.firstACTime = secondsToSolve;
                }
            }
        }

        // Convert to leaderboard entries and sort
        List<LeaderboardEntry> entries = userStatsMap.values().stream()
                .map(stats -> new LeaderboardEntry(
                        0, // Rank will be set after sorting
                        stats.userId,
                        stats.userName,
                        stats.solvedProblems.size(),
                        stats.firstACTime != null ? stats.firstACTime : Long.MAX_VALUE
                ))
                .sorted((a, b) -> {
                    // Sort by: 1) More problems solved, 2) Earlier first AC time
                    int problemCompare = b.getProblemsSolved().compareTo(a.getProblemsSolved());
                    if (problemCompare != 0) return problemCompare;
                    return a.getTimeToFirstAC().compareTo(b.getTimeToFirstAC());
                })
                .collect(Collectors.toList());

        // Assign ranks
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        return new LeaderboardResponse(
                contest.getId(),
                contest.getTitle(),
                entries
        );
    }

    private static class UserStats {
        Long userId;
        String userName;
        Set<Long> solvedProblems = new HashSet<>();
        Long firstACTime = null;

        UserStats(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }
}