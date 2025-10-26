package com.codeplatform.service;

import com.codeplatform.dto.SubmissionRequest;
import com.codeplatform.dto.SubmissionResponse;
import com.codeplatform.dto.SubmissionStatusResponse;
import com.codeplatform.entity.*;
import com.codeplatform.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final ProblemService problemService;
    private final ContestService contestService;
    private final JudgeService judgeService;

    @Transactional
    public SubmissionResponse submitCode(SubmissionRequest request) {
        // Validate entities exist
        User user = userService.getUserById(request.getUserId());
        Problem problem = problemService.getProblemEntityById(request.getProblemId());
        Contest contest = contestService.getContestEntityById(request.getContestId());

        // Create submission
        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setContest(contest);
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage().toUpperCase());
        submission.setStatus("PENDING");

        Submission savedSubmission = submissionRepository.save(submission);

        // Process asynchronously
        processSubmissionAsync(savedSubmission.getId());

        return new SubmissionResponse(
                savedSubmission.getId(),
                "PENDING",
                "Submission queued for processing"
        );
    }

    @Async
    public void processSubmissionAsync(Long submissionId) {
        try {
            Thread.sleep(1000); // Simulate queue delay

            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));

            // Update status to RUNNING
            submission.setStatus("RUNNING");
            submissionRepository.save(submission);

            // Execute code
            long startTime = System.currentTimeMillis();

            String result = judgeService.executeCode(
                    submission.getCode(),
                    submission.getLanguage(),
                    submission.getProblem()
            );

            long endTime = System.currentTimeMillis();
            int executionTime = (int) (endTime - startTime);

            // Update submission with result
            if (result.equals("ACCEPTED")) {
                submission.setStatus("ACCEPTED");
                submission.setResult("All test cases passed!");
            } else if (result.startsWith("WRONG_ANSWER")) {
                submission.setStatus("WRONG_ANSWER");
                submission.setResult(result);
            } else if (result.contains("TIME_LIMIT_EXCEEDED")) {
                submission.setStatus("TIME_LIMIT_EXCEEDED");
                submission.setResult("Time limit exceeded");
            } else if (result.startsWith("COMPILATION_ERROR")) {
                submission.setStatus("COMPILATION_ERROR");
                submission.setResult(result);
            } else {
                submission.setStatus("RUNTIME_ERROR");
                submission.setResult(result);
            }

            submission.setExecutionTime(executionTime);
            submissionRepository.save(submission);

            log.info("Submission {} processed: {}", submissionId, submission.getStatus());

        } catch (Exception e) {
            log.error("Error processing submission {}: {}", submissionId, e.getMessage());

            Submission submission = submissionRepository.findById(submissionId)
                    .orElse(null);

            if (submission != null) {
                submission.setStatus("RUNTIME_ERROR");
                submission.setResult("Internal error: " + e.getMessage());
                submissionRepository.save(submission);
            }
        }
    }

    @Transactional(readOnly = true)
    public SubmissionStatusResponse getSubmissionStatus(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        return new SubmissionStatusResponse(
                submission.getId(),
                submission.getStatus(),
                submission.getResult(),
                submission.getExecutionTime(),
                submission.getSubmittedAt()
        );
    }
}