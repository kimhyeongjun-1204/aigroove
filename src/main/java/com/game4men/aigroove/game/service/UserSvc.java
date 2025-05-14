package com.game4men.aigroove.game.service;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.*;
import com.game4men.aigroove.common.utils.JwtUtils;
import com.game4men.aigroove.game.DTO.JwtResponse;
import com.game4men.aigroove.game.DTO.LoginRequest;
import com.game4men.aigroove.game.DTO.UserDTO;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserSvc {
    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final BadgeSvc badgeService;

    @Autowired
    public UserSvc(UserRepository userRepository, InquiryRepository inquiryRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, BadgeSvc badgeService) {
        this.userRepository = userRepository;
        this.inquiryRepository = inquiryRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.badgeService = badgeService;
    }
    
    public JwtResponse login(LoginRequest loginRequest) {
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + loginRequest.getUsername()));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getHashed_password())) {
            // return null;
            throw new RuntimeException("Invalid password");
        }    
        String token = jwtUtils.generateToken(user.getUsername());
        
        return new JwtResponse(token, loginRequest.getUsername(), user.getIs_tutorial_complete(), "Bearer");
    }

    public void signup(UserDTO request){
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) throw new DuplicateKeyException(null);
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setHashed_password(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        
        userRepository.save(user);
        createDefaultBadgesForUser(user);
    }
    
    public void completeTutorial(User user){
        user.setIs_tutorial_complete(true);
        userRepository.save(user);
        return;
    }

    @Transactional
    public void deleteAccount(User user){
        inquiryRepository.deleteByUser(user);
        userRepository.deleteById(user.getUser_id());
    }
    
    private void createDefaultBadgesForUser(User user) {
        // 1~20번 뱃지 생성
        for (int badgeCode = 1; badgeCode <= 20; badgeCode++) {
            // 모든 뱃지는 초기값 0으로 시작하고, 달성하지 않은 상태로 설정
            badgeService.createBadgeForUser(user, badgeCode, 0, false);
        }
    }
}