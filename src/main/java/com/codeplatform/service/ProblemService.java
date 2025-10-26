package com.codeplatform.service;

import com.codeplatform.dto.ProblemResponse;
import com.codeplatform.entity.Problem;
import com.codeplatform.entity.TestCase;
import com.codeplatform.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    @Transactional(readOnly = true)
    public ProblemResponse getProblemById(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        // Only return sample test cases to users
        List<ProblemResponse.SampleTestCase> sampleTestCases = problem.getTestCases()
                .stream()
                .filter(TestCase::getIsSample)
                .map(tc -> new ProblemResponse.SampleTestCase(tc.getInput(), tc.getExpectedOutput()))
                .collect(Collectors.toList());

        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getDifficulty(),
                problem.getTimeLimit(),
                problem.getMemoryLimit(),
                sampleTestCases
        );
    }

    @Transactional(readOnly = true)
    public List<ProblemResponse> getProblemsByContestId(Long contestId) {
        return problemRepository.findByContestId(contestId)
                .stream()
                .map(problem -> {
                    List<ProblemResponse.SampleTestCase> sampleTestCases = problem.getTestCases()
                            .stream()
                            .filter(TestCase::getIsSample)
                            .map(tc -> new ProblemResponse.SampleTestCase(tc.getInput(), tc.getExpectedOutput()))
                            .collect(Collectors.toList());

                    return new ProblemResponse(
                            problem.getId(),
                            problem.getTitle(),
                            problem.getDescription(),
                            problem.getDifficulty(),
                            problem.getTimeLimit(),
                            problem.getMemoryLimit(),
                            sampleTestCases
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Problem getProblemEntityById(Long problemId) {
        return problemRepository.findByIdWithTestCases(problemId);
    }
}