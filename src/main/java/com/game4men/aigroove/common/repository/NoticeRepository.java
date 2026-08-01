package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findByAuthorAdminIdOrderByCreatedAtDesc(Integer adminId);
    Optional<Notice> findTopByOrderByCreatedAtDesc();

    // N+1 해결: Notice 목록 조회 시 Author(Admin)를 한 번에 fetch하여 개별 SELECT 방지
    @Query("SELECT n FROM Notice n JOIN FETCH n.author ORDER BY n.noticeId DESC")
    List<Notice> findAllWithAuthor();
} 