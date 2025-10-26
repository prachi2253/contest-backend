package com.codeplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(length = 5000, nullable = false)
    private String input;

    @Column(name = "expected_output", length = 5000, nullable = false)
    private String expectedOutput;

    @Column(name = "is_sample", nullable = false)
    private Boolean isSample; // true = visible to users, false = hidden
}