package com.game4men.aigroove.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.game4men.aigroove.common.entity.Badge;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.BadgeRepository;
import com.game4men.aigroove.game.DTO.BadgeDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeSvc {
    public final BadgeRepository badgeRepository;

    @Transactional
    public void updateBadgeStatus(List<BadgeDTO> badgeDTOList, User user) {
        if (badgeDTOList == null || badgeDTOList.isEmpty()) {
            return;
        }

        for (BadgeDTO badgeDTO : badgeDTOList) {

            // 해당 사용자와 뱃지 코드로 뱃지 찾기
            Optional<Badge> optionalBadge = badgeRepository.findByUserAndBadgeCode(user, badgeDTO.getCode());

            if (optionalBadge.isPresent()) {
                // 기존 뱃지가 있으면 상태 업데이트
                Badge badge = optionalBadge.get();

                // 현재 값이 DTO의 값보다 작을 때만 업데이트 (이미 더 높은 값이면 유지)
                if (badge.getCurrentValue() < badgeDTO.getCurrentValue()) {
                    badge.setCurrentValue(badgeDTO.getCurrentValue());
                }

                // 달성 여부가 true로 변경된 경우에만 업데이트 (한 번 달성하면 유지)
                if (badgeDTO.getHasAchieved() && !badge.getHasAchieved()) {
                    badge.setHasAchieved(true);
                }

                badgeRepository.save(badge);
            } else {
                // 해당 뱃지가 없는 경우 새로 생성 (비정상 케이스지만 안전을 위해 처리)
                Badge newBadge = new Badge();
                newBadge.setUser(user);
                newBadge.setBadgeCode(badgeDTO.getCode());
                newBadge.setCurrentValue(badgeDTO.getCurrentValue());
                newBadge.setHasAchieved(badgeDTO.getHasAchieved());

                badgeRepository.save(newBadge);
            }
        }
    }

    /**
     * 모든 뱃지 조회
     */
    public List<Badge> findAllBadges() {
        return badgeRepository.findAll();
    }

    /**
     * 뱃지 ID로 뱃지 조회
     */
    public Optional<Badge> findBadgeById(Integer badgeId) {
        return badgeRepository.findById(badgeId);
    }

    /**
     * 사용자의 모든 뱃지 조회
     */
    public List<BadgeDTO> findBadgesByUser(User user) {
        List<Badge> badges = badgeRepository.findByUser(user);
        List<BadgeDTO> list = new ArrayList<BadgeDTO>();
        for (Badge badge : badges) {
            BadgeDTO dto = new BadgeDTO();
            dto.setCode(badge.getBadgeCode());
            dto.setCurrentValue(badge.getCurrentValue());
            dto.setHasAchieved(badge.getHasAchieved());
            list.add(dto);
        }
        return list;
    }

    /**
     * 사용자와 뱃지 코드로 뱃지 조회
     */
    public BadgeDTO findBadgeByUserAndBadgeCode(User user, Integer badgeCode) {
        Badge badge = badgeRepository.findByUserAndBadgeCode(user, badgeCode).get();
        BadgeDTO dto = new BadgeDTO();
        dto.setCode(badge.getBadgeCode());
        dto.setCurrentValue(badge.getCurrentValue());
        dto.setHasAchieved(badge.getHasAchieved());
        return dto;
    }

    /**
     * 새 뱃지 생성
     */
    @Transactional
    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    /**
     * 사용자에게 새 뱃지 생성 및 할당
     */
    @Transactional
    public Badge createBadgeForUser(User user, Integer badgeCode, Integer initialValue, Boolean hasAchieved) {
        Badge badge = new Badge();
        badge.setUser(user);
        badge.setBadgeCode(badgeCode);
        badge.setCurrentValue(initialValue);
        badge.setHasAchieved(hasAchieved);

        return badgeRepository.save(badge);
    }

    /**
     * 뱃지 정보 업데이트
     */
    @Transactional
    public Badge updateBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    /**
     * 뱃지 진행 상태 업데이트
     */
    @Transactional
    public Badge updateBadgeProgress(Integer badgeId, Integer newValue, Boolean hasAchieved) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + badgeId));

        badge.setCurrentValue(newValue);
        badge.setHasAchieved(hasAchieved);

        return badgeRepository.save(badge);
    }

    /**
     * 뱃지 달성 여부 업데이트
     */
    @Transactional
    public Badge updateBadgeAchievement(Integer badgeId, Boolean hasAchieved) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + badgeId));

        badge.setHasAchieved(hasAchieved);

        return badgeRepository.save(badge);
    }

    /**
     * 뱃지 삭제
     */
    @Transactional
    public void deleteBadge(Integer badgeId) {
        badgeRepository.deleteById(badgeId);
    }

    /**
     * 사용자의 모든 뱃지 삭제
     */
    @Transactional
    public void deleteAllBadgesByUser(User user) {
        List<Badge> badges = badgeRepository.findByUser(user);
        badgeRepository.deleteAll(badges);
    }

    /**
     * 특정 뱃지 코드의 뱃지 삭제
     */
    @Transactional
    public void deleteBadgeByUserAndBadgeCode(User user, Integer badgeCode) {
        Optional<Badge> badge = badgeRepository.findByUserAndBadgeCode(user, badgeCode);
        badge.ifPresent(badgeRepository::delete);
    }
}
