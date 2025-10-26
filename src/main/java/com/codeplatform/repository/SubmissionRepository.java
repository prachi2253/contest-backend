package com.codeplatform.repository;

import com.codeplatform.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Find all submissions by user in a contest
    List<Submission> findByUserIdAndContestId(Long userId, Long contestId);

    // Find all submissions for a problem by a user
    List<Submission> findByUserIdAndProblemId(Long userId, Long problemId);

    // Find all accepted submissions in a contest (for leaderboard)
    @Query("SELECT s FROM Submission s WHERE s.contest.id = :contestId AND s.status = 'ACCEPTED' ORDER BY s.submittedAt ASC")
    List<Submission> findAcceptedSubmissionsByContestId(Long contestId);

    // Check if user has already solved a problem (has an ACCEPTED submission)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Submission s WHERE s.user.id = :userId AND s.problem.id = :problemId AND s.status = 'ACCEPTED'")
    boolean hasUserSolvedProblem(Long userId, Long problemId);

    // Find first accepted submission for a user-problem pair
    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId AND s.problem.id = :problemId AND s.status = 'ACCEPTED' ORDER BY s.submittedAt ASC")
    List<Submission> findFirstAcceptedSubmission(Long userId, Long problemId);
}