package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.Inquiry;
import com.game4men.aigroove.common.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    void deleteByUser(User user);
} 