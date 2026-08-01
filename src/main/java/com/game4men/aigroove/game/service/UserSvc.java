package com.game4men.aigroove.game.service;

import com.game4men.aigroove.common.entity.DailyLog;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.*;
import com.game4men.aigroove.common.utils.JwtUtils;
import com.game4men.aigroove.game.DTO.JwtResponse;
import com.game4men.aigroove.game.DTO.LoginRequest;
import com.game4men.aigroove.game.DTO.UserDTO;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSvc {
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final DailyLogRepository dailyLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final BadgeSvc badgeService;

    public JwtResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + loginRequest.getUsername()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getHashed_password())) {
            // return null;
            throw new RuntimeException("Invalid password");
        }
        String token = jwtUtils.generateToken(user.getUsername());

        LocalDate today = LocalDate.now();
        // 기존 로그 있는지 확인
        DailyLog log = dailyLogRepository.findByLogDate(today)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setLogDate(today);
                    newLog.setDailyUsers(0);
                    newLog.setSongUploads(0);
                    newLog.setInquirys(0);
                    return newLog;
                });
        int dailyUsers = log.getDailyUsers();
        log.setDailyUsers(dailyUsers + 1);
        dailyLogRepository.save(log);

        return new JwtResponse(token, loginRequest.getUsername(), user.getIs_tutorial_complete(), "Bearer");
    }

    public User signup(UserDTO request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent())
            throw new DuplicateKeyException(null);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setHashed_password(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());

        userRepository.save(user);
        createDefaultBadgesForUser(user);
        return user;
    }

    public void completeTutorial(User user) {
        user.setIs_tutorial_complete(true);
        userRepository.save(user);
        return;
    }

    @Transactional
    public void deleteAccount(User user) {
        System.err.println(1);
        inquiryRepository.deleteByUser(user);
        System.err.println(user.getUser_id());
        userRepository.deleteById(user.getUser_id());
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }

    private void createDefaultBadgesForUser(User user) {
        // 1~20번 뱃지 생성
        for (int badgeCode = 1; badgeCode <= 20; badgeCode++) {
            // 모든 뱃지는 초기값 0으로 시작하고, 달성하지 않은 상태로 설정
            badgeService.createBadgeForUser(user, badgeCode, 0, false);
        }
    }

}