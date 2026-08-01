package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.UserResponse;
import com.game4men.aigroove.admin.service.AdminUserSvc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserSvc userService;

    // 1. 사용자 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserResponse> users = userService.findAllUsers();
            response.put("result_code", 200);
            response.put("success", true);
            response.put("users", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("success", false);
            response.put("message", "사용자 목록을 불러오는데 실패했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

//    // 2. 사용자 상세 정보 조회
//    @GetMapping("/detail/{userId}")
//    public ResponseEntity<Map<String, Object>> getUserDetail(
//            @PathVariable Integer userId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            Map<String, Object> userDetail = userService.getUserDetail(userId);
//            response.put("result_code", 200);
//            response.put("success", true);
//            response.putAll(userDetail);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("result_code", 400);
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    // 3. 사용자 검색
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserResponse> users = userService.searchUsers(keyword);
            response.put("result_code", 200);
            response.put("success", true);
            response.put("users", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 4. 사용자 삭제
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable Integer userId,
            @RequestParam("login_id") String loginId) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.deleteUser(userId, loginId);
            response.put("result_code", 200);
            response.put("success", true);
            response.put("message", "사용자가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("result_code", 400);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("success", false);
            response.put("message", "사용자 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
