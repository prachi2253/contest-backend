package com.codeplatform.repository;

import com.codeplatform.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // Find all problems in a contest
    List<Problem> findByContestId(Long contestId);

    // Find problem by ID and fetch with test cases
    @Query("SELECT p FROM Problem p LEFT JOIN FETCH p.testCases WHERE p.id = :problemId")
    Problem findByIdWithTestCases(Long problemId);
}