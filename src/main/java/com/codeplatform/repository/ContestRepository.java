package com.codeplatform.repository;

import com.codeplatform.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    // Find active contests (between start and end time)
    List<Contest> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime now1, LocalDateTime now2);

    // Find all contests ordered by start time
    List<Contest> findAllByOrderByStartTimeDesc();
}