package com.codeplatform.service;

import com.codeplatform.entity.Problem;
import com.codeplatform.entity.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JudgeService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/code-judge/";
    private static final long TIMEOUT_SECONDS = 10; // Overall timeout per test case

    public String executeCode(String code, String language, Problem problem) {
        String executionId = UUID.randomUUID().toString();
        String workDir = TEMP_DIR + executionId + "/";

        try {
            // Create working directory
            Files.createDirectories(Paths.get(workDir));

            // Run against all test cases
            for (TestCase testCase : problem.getTestCases()) {
                String result = runTestCase(code, language, testCase, workDir, problem.getTimeLimit());

                if (!result.equals("ACCEPTED")) {
                    return result; // Return first failure
                }
            }

            return "ACCEPTED";

        } catch (Exception e) {
            log.error("Execution failed for {}: {}", executionId, e.getMessage());
            return "RUNTIME_ERROR: " + e.getMessage();
        } finally {
            // Cleanup
            cleanupDirectory(workDir);
        }
    }

    private String runTestCase(String code, String language, TestCase testCase,
                               String workDir, int timeLimitSeconds) throws Exception {

        // Write code to file
        String fileName = getFileName(language);
        String filePath = workDir + fileName;
        Files.writeString(Paths.get(filePath), code);

        // Write input to file
        String inputPath = workDir + "input.txt";
        Files.writeString(Paths.get(inputPath), testCase.getInput());

        // Compile if needed
        String compileResult = compile(language, workDir, fileName);
        if (compileResult != null) {
            return "COMPILATION_ERROR: " + compileResult;
        }

        // Execute
        String output = execute(language, workDir, inputPath, timeLimitSeconds);

        // Validate output
        String expectedOutput = testCase.getExpectedOutput().trim();
        String actualOutput = output.trim();

        if (expectedOutput.equals(actualOutput)) {
            return "ACCEPTED";
        } else {
            return "WRONG_ANSWER: Expected '" + expectedOutput + "' but got '" + actualOutput + "'";
        }
    }

    private String getFileName(String language) {
        return switch (language.toUpperCase()) {
            case "CPP" -> "solution.cpp";
            case "JAVA" -> "Solution.java";
            case "PYTHON" -> "solution.py";
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

    private String compile(String language, String workDir, String fileName) throws Exception {
        if (!language.equalsIgnoreCase("CPP") && !language.equalsIgnoreCase("JAVA")) {
            return null; // Python doesn't need compilation
        }

        ProcessBuilder pb;
        if (language.equalsIgnoreCase("CPP")) {
            pb = new ProcessBuilder("docker", "run", "--rm",
                    "-v", workDir + ":/workspace",
                    "-w", "/workspace",
                    "--memory", "256m",
                    "--cpus", "1",
                    "gcc:latest",
                    "g++", "-o", "solution", fileName);
        } else { // JAVA
            pb = new ProcessBuilder("docker", "run", "--rm",
                    "-v", workDir + ":/workspace",
                    "-w", "/workspace",
                    "--memory", "256m",
                    "--cpus", "1",
                    "openjdk:17-slim",
                    "javac", fileName);
        }

        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output = readStream(process.getInputStream());
        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            return "Compilation timeout";
        }

        if (process.exitValue() != 0) {
            return output;
        }

        return null; // Success
    }

    private String execute(String language, String workDir, String inputPath,
                           int timeLimitSeconds) throws Exception {

        ProcessBuilder pb;

        switch (language.toUpperCase()) {
            case "CPP" -> pb = new ProcessBuilder("docker", "run", "--rm",
                    "-v", workDir + ":/workspace",
                    "-w", "/workspace",
                    "--memory", "256m",
                    "--cpus", "1",
                    "--network", "none",
                    "gcc:latest",
                    "timeout", timeLimitSeconds + "s",
                    "./solution");

            case "JAVA" -> pb = new ProcessBuilder("docker", "run", "--rm",
                    "-v", workDir + ":/workspace",
                    "-w", "/workspace",
                    "--memory", "256m",
                    "--cpus", "1",
                    "--network", "none",
                    "openjdk:17-slim",
                    "timeout", timeLimitSeconds + "s",
                    "java", "Solution");

            case "PYTHON" -> pb = new ProcessBuilder("docker", "run", "--rm",
                    "-v", workDir + ":/workspace",
                    "-w", "/workspace",
                    "--memory", "256m",
                    "--cpus", "1",
                    "--network", "none",
                    "python:3.11-slim",
                    "timeout", timeLimitSeconds + "s",
                    "python3", "solution.py");

            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        }

        pb.redirectInput(new File(inputPath));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        String output = readStream(process.getInputStream());
        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }

        if (process.exitValue() != 0) {
            if (output.contains("timeout") || process.exitValue() == 124) {
                throw new RuntimeException("TIME_LIMIT_EXCEEDED");
            }
            throw new RuntimeException("RUNTIME_ERROR: " + output);
        }

        return output;
    }

    private String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }

    private void cleanupDirectory(String workDir) {
        try {
            Path path = Paths.get(workDir);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.warn("Failed to delete: {}", p);
                            }
                        });
            }
        } catch (IOException e) {
            log.error("Cleanup failed: {}", e.getMessage());
        }
    }
}