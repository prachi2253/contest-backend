package com.codeplatform.config;

import com.codeplatform.entity.Contest;
import com.codeplatform.entity.Problem;
import com.codeplatform.entity.TestCase;
import com.codeplatform.repository.ContestRepository;
import com.codeplatform.repository.ProblemRepository;
import com.codeplatform.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ContestRepository contestRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing sample data...");

        // Create Contest
        Contest contest = new Contest();
        contest.setTitle("Weekly Coding Contest #1");
        contest.setDescription("Welcome to our first weekly coding contest! Solve problems and climb the leaderboard!");
        contest.setStartTime(LocalDateTime.now().minusHours(1)); // Started 1 hour ago
        contest.setEndTime(LocalDateTime.now().plusHours(2)); // Ends in 2 hours
        contest = contestRepository.save(contest);
        log.info("Created contest: {}", contest.getTitle());

        // Problem 1: Two Sum
        Problem problem1 = createTwoSumProblem(contest);
        problemRepository.save(problem1);
        log.info("Created problem: {}", problem1.getTitle());

        // Problem 2: Factorial
        Problem problem2 = createFactorialProblem(contest);
        problemRepository.save(problem2);
        log.info("Created problem: {}", problem2.getTitle());

        // Problem 3: Palindrome Check
        Problem problem3 = createPalindromeProblem(contest);
        problemRepository.save(problem3);
        log.info("Created problem: {}", problem3.getTitle());

        log.info("Sample data initialization completed!");
    }

    private Problem createTwoSumProblem(Contest contest) {
        Problem problem = new Problem();
        problem.setContest(contest);
        problem.setTitle("Two Sum");
        problem.setDescription(
                "Given an array of integers nums and an integer target, return the two numbers that add up to target.\n\n" +
                        "Input Format:\n" +
                        "- First line: n (number of elements)\n" +
                        "- Second line: n space-separated integers\n" +
                        "- Third line: target integer\n\n" +
                        "Output Format:\n" +
                        "- Two space-separated integers that sum to target\n\n" +
                        "Example:\n" +
                        "Input:\n" +
                        "4\n" +
                        "2 7 11 15\n" +
                        "9\n\n" +
                        "Output:\n" +
                        "2 7"
        );
        problem.setDifficulty("EASY");
        problem.setTimeLimit(2);
        problem.setMemoryLimit(256);

        List<TestCase> testCases = new ArrayList<>();

        // Sample Test Case 1
        TestCase tc1 = new TestCase();
        tc1.setProblem(problem);
        tc1.setInput("4\n2 7 11 15\n9");
        tc1.setExpectedOutput("2 7");
        tc1.setIsSample(true);
        testCases.add(tc1);

        // Sample Test Case 2
        TestCase tc2 = new TestCase();
        tc2.setProblem(problem);
        tc2.setInput("3\n3 2 4\n6");
        tc2.setExpectedOutput("2 4");
        tc2.setIsSample(true);
        testCases.add(tc2);

        // Hidden Test Case 1
        TestCase tc3 = new TestCase();
        tc3.setProblem(problem);
        tc3.setInput("5\n1 5 3 7 9\n12");
        tc3.setExpectedOutput("3 9");
        tc3.setIsSample(false);
        testCases.add(tc3);

        // Hidden Test Case 2
        TestCase tc4 = new TestCase();
        tc4.setProblem(problem);
        tc4.setInput("2\n10 20\n30");
        tc4.setExpectedOutput("10 20");
        tc4.setIsSample(false);
        testCases.add(tc4);

        problem.setTestCases(testCases);
        return problem;
    }

    private Problem createFactorialProblem(Contest contest) {
        Problem problem = new Problem();
        problem.setContest(contest);
        problem.setTitle("Factorial");
        problem.setDescription(
                "Calculate the factorial of a given number n.\n\n" +
                        "Input Format:\n" +
                        "- Single integer n (0 ≤ n ≤ 20)\n\n" +
                        "Output Format:\n" +
                        "- Single integer representing n!\n\n" +
                        "Example:\n" +
                        "Input:\n" +
                        "5\n\n" +
                        "Output:\n" +
                        "120"
        );
        problem.setDifficulty("EASY");
        problem.setTimeLimit(1);
        problem.setMemoryLimit(128);

        List<TestCase> testCases = new ArrayList<>();

        // Sample Test Case 1
        TestCase tc1 = new TestCase();
        tc1.setProblem(problem);
        tc1.setInput("5");
        tc1.setExpectedOutput("120");
        tc1.setIsSample(true);
        testCases.add(tc1);

        // Sample Test Case 2
        TestCase tc2 = new TestCase();
        tc2.setProblem(problem);
        tc2.setInput("0");
        tc2.setExpectedOutput("1");
        tc2.setIsSample(true);
        testCases.add(tc2);

        // Hidden Test Case 1
        TestCase tc3 = new TestCase();
        tc3.setProblem(problem);
        tc3.setInput("10");
        tc3.setExpectedOutput("3628800");
        tc3.setIsSample(false);
        testCases.add(tc3);

        // Hidden Test Case 2
        TestCase tc4 = new TestCase();
        tc4.setProblem(problem);
        tc4.setInput("3");
        tc4.setExpectedOutput("6");
        tc4.setIsSample(false);
        testCases.add(tc4);

        problem.setTestCases(testCases);
        return problem;
    }

    private Problem createPalindromeProblem(Contest contest) {
        Problem problem = new Problem();
        problem.setContest(contest);
        problem.setTitle("Palindrome Check");
        problem.setDescription(
                "Check if a given string is a palindrome (reads the same forwards and backwards).\n\n" +
                        "Input Format:\n" +
                        "- Single line containing a string (only lowercase letters, no spaces)\n\n" +
                        "Output Format:\n" +
                        "- 'YES' if palindrome, 'NO' otherwise\n\n" +
                        "Example:\n" +
                        "Input:\n" +
                        "racecar\n\n" +
                        "Output:\n" +
                        "YES"
        );
        problem.setDifficulty("MEDIUM");
        problem.setTimeLimit(1);
        problem.setMemoryLimit(128);

        List<TestCase> testCases = new ArrayList<>();

        // Sample Test Case 1
        TestCase tc1 = new TestCase();
        tc1.setProblem(problem);
        tc1.setInput("racecar");
        tc1.setExpectedOutput("YES");
        tc1.setIsSample(true);
        testCases.add(tc1);

        // Sample Test Case 2
        TestCase tc2 = new TestCase();
        tc2.setProblem(problem);
        tc2.setInput("hello");
        tc2.setExpectedOutput("NO");
        tc2.setIsSample(true);
        testCases.add(tc2);

        // Hidden Test Case 1
        TestCase tc3 = new TestCase();
        tc3.setProblem(problem);
        tc3.setInput("noon");
        tc3.setExpectedOutput("YES");
        tc3.setIsSample(false);
        testCases.add(tc3);

        // Hidden Test Case 2
        TestCase tc4 = new TestCase();
        tc4.setProblem(problem);
        tc4.setInput("world");
        tc4.setExpectedOutput("NO");
        tc4.setIsSample(false);
        testCases.add(tc4);

        problem.setTestCases(testCases);
        return problem;
    }
}