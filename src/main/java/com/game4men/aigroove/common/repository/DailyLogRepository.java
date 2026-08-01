package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, LocalDate> {
    List<DailyLog> findByLogDateBetweenOrderByLogDateDesc(LocalDate startDate, LocalDate endDate);
    Optional<DailyLog> findByLogDate(LocalDate logDate);
} 