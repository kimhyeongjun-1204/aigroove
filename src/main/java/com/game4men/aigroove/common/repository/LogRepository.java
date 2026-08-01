package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log , Integer> {
    List<Log> findAllByOrderByLogIdDesc();

    // N+1 해결: Log 조회 시 Admin, User를 한 번에 fetch하여 개별 SELECT 방지
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.admin LEFT JOIN FETCH l.user ORDER BY l.logId DESC")
    List<Log> findAllWithUsersOrderByLogIdDesc();
}

