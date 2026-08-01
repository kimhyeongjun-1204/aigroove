package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.Inquiry;
import com.game4men.aigroove.common.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    void deleteByUser(User user);

    // N+1 해결: Inquiry 목록 조회 시 User, Admin을 한 번에 fetch하여 개별 SELECT 방지
    @Query("SELECT i FROM Inquiry i LEFT JOIN FETCH i.user LEFT JOIN FETCH i.answeredAdmin ORDER BY i.inquiryId DESC")
    List<Inquiry> findAllWithUserAndAdmin();
} 