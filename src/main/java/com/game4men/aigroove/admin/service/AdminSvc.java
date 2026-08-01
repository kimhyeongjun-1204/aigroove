package com.game4men.aigroove.admin.service;

import com.game4men.aigroove.admin.dto.AdminResponse;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSvc {
    private final AdminRepository adminRepository;
    private final LogSvc logSvc;

    // 1. 관리자 조회
    @Transactional(readOnly = true)
    public List<AdminResponse> findAllAdmins() {
        return adminRepository.findAll().stream()
                .filter(admin -> !admin.getIsDelete())  // isDelete가 false인 관리자만 필터링
                .map(AdminResponse::new)
                .collect(Collectors.toList());
    }

    // 2. 관리자 탈퇴 , 관리자 승인 거절
    @Transactional
    public void deleteAdmin(Integer adminId, String loginId) {
        Admin targetAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다. ID: " + adminId));
        
        Admin loginAdmin = adminRepository.findByUsername(loginId)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 관리자를 찾을 수 없습니다. Username: " + loginId));
        
        targetAdmin.setIsDelete(true);
        adminRepository.save(targetAdmin);

        // 로그 기록
        logSvc.saveLog(
            targetAdmin.getName() + " 관리자를 삭제하였습니다.",
            loginAdmin,
            Log.LogLevel.INFO
        );
    }

    // 3. 관리자 승인
    @Transactional
    public void approveAdmin(Integer adminId, String role, String loginId) {
        Admin targetAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다"));

        Admin loginAdmin = adminRepository.findById(Integer.parseInt(loginId))
                .orElseThrow(() -> new IllegalArgumentException("로그인한 관리자를 찾을 수 없습니다. Username: " + loginId));

        targetAdmin.setSignupDate(LocalDate.now());
        if(role.equals("AI")) {
            targetAdmin.setRole(Admin.Role.AI);
        }else {
            targetAdmin.setRole(Admin.Role.USER);
        }
        adminRepository.save(targetAdmin);

        // 로그 기록
        logSvc.saveLog(
            targetAdmin.getName() + " 관리자를 " + role + " 권한으로 승인하였습니다.",
            loginAdmin,
            Log.LogLevel.INFO
        );
    }

    @Transactional(readOnly = true)
    public Admin findAdmin(Integer adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다. ID: " + adminId));
    }

    @Transactional(readOnly = true)
    public Admin findAdminByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 관리자를 찾을 수 없습니다. Username: " + username));
    }
} 