package com.codeplatform.service;

import com.codeplatform.dto.ContestResponse;
import com.codeplatform.entity.Contest;
import com.codeplatform.entity.Problem;
import com.codeplatform.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;

    @Transactional(readOnly = true)
    public ContestResponse getContestById(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        return mapToResponse(contest);
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getAllContests() {
        return contestRepository.findAllByOrderByStartTimeDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getActiveContests() {
        return contestRepository.findActiveContests(LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Contest getContestEntityById(Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
    }

    private ContestResponse mapToResponse(Contest contest) {
        List<ContestResponse.ProblemSummary> problemSummaries = contest.getProblems()
                .stream()
                .map(p -> new ContestResponse.ProblemSummary(p.getId(), p.getTitle(), p.getDifficulty()))
                .collect(Collectors.toList());

        return new ContestResponse(
                contest.getId(),
                contest.getTitle(),
                contest.getDescription(),
                contest.getStartTime(),
                contest.getEndTime(),
                problemSummaries
        );
    }
}