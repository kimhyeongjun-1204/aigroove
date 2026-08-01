package com.game4men.aigroove.admin.service;

import com.game4men.aigroove.admin.dto.UserResponse;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserSvc {
    private final UserRepository userRepository;
    private final GameStatusRepository gameStatusRepository;
    private final GameRoomRepository gameRoomRepository;
    private final InquiryRepository inquiryRepository;
    private final LogSvc logSvc;
    private final AdminRepository adminRepository;

    // 모든 사용자 조회
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getIsDelete())  // isDelete가 false인 사용자만 필터링
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(Integer userId, String loginId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Admin loginAdmin = adminRepository.findByUsername(loginId)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 관리자를 찾을 수 없습니다. Username: " + loginId));

        targetUser.setIsDelete(true);
        userRepository.save(targetUser);

        // 로그 기록
        logSvc.saveLog(
            targetUser.getNickname() + " 사용자를 탈퇴 처리하였습니다.",
            loginAdmin,
            Log.LogLevel.INFO
        );
    }

    // User 엔티티를 UserResponse DTO로 변환
    private UserResponse convertToDto(User user) {
        return UserResponse.builder()
                .userId(user.getUser_id())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public List<UserResponse> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingOrNicknameContainingOrEmailContaining(
                keyword, keyword, keyword)
                .stream()
                .filter(user -> !user.getIsDelete())  // isDelete가 false인 사용자만 필터링
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // @Transactional(readOnly = true)
    // public Map<String, Object> getUserDetail(Integer userId) {
    // // 1. 기본 사용자 정보 조회
    // User user = userRepository.findById(userId)
    // .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    //
    // // 2. 플레이 타임 계산 (GameStatus 테이블에서)
    // int totalPlayTime = gameStatusRepository.findByUser_UserId(userId)
    // .stream()
    // .mapToInt(status -> calculatePlayTime(status))
    // .sum();
    //
    // // 3. 등록한 곡 수 계산 (GameRoom 테이블에서 host_id가 userId인 경우)
    // int songNumber = gameRoomRepository.countByHostId(userId);
    //
    // // 4. 문의글 수 계산 (Inquiry 테이블에서)
    // int inquiryNumber = inquiryRepository.countByUserId(userId);
    //
    // // 5. 결과 맵 생성
    // Map<String, Object> result = new HashMap<>();
    // result.put("nickname", user.getNickname());
    // result.put("play_time", totalPlayTime);
    // result.put("song_number", songNumber);
    // result.put("inquiry_number", inquiryNumber);
    //
    // return result;
    // }
    //
    // private int calculatePlayTime(GameStatus status) {
    // // 게임 진행도에 따른 플레이 타임 계산 로직
    // // 예: 진행도 1.0이 3분이라고 가정하면
    // return (int) (status.getCurrentProgress() * 180);
    // }
}