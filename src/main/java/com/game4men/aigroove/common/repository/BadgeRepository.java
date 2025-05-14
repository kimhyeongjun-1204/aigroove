package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.Badge;
import com.game4men.aigroove.common.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
        // 특정 사용자의 모든 뱃지 찾기
        List<Badge> findByUser(User user);
    
        // 특정 사용자와 뱃지 코드로 뱃지 찾기
        Optional<Badge> findByUserAndBadgeCode(User user, Integer badgeCode);
        
        // 달성한 뱃지만 찾기
        List<Badge> findByHasAchievedTrue();
        
        // 특정 사용자의 달성한 뱃지 찾기
        List<Badge> findByUserAndHasAchievedTrue(User user);
        
        // 특정 뱃지 코드로 모든 뱃지 찾기
        List<Badge> findByBadgeCode(Integer badgeCode);
}